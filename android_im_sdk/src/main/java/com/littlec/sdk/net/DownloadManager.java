package com.littlec.sdk.net;

import android.content.Context;
import android.util.Log;

import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.dao.DownloadDBEntityDao;
import com.littlec.sdk.database.entity.DownloadDBEntity;

import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by zhangguoqiong on 2016/7/26.
 */
 class DownloadManager {
    private static final String TAG = "DownloadManager";
    private Context context;
    private static DownloadManager downloadManager;
    private static DownloadDBEntityDao downloadDao;
    private int mPoolSize = 5;
    private ExecutorService executorService;
    private Map<String, Future> futureMap;
    private OkHttpClient client;

    public Map<String, DownloadTask> getCurrentTaskList() {
        return currentTaskList;
    }

    private Map<String, DownloadTask> currentTaskList = new HashMap<>();

    private void init(OkHttpClient okHttpClient) {
        executorService = Executors.newFixedThreadPool(mPoolSize);
        futureMap = new HashMap<>();
        downloadDao = DBFactory.getDBManager().getDBDownloadService();
        if (okHttpClient != null) {
            client = okHttpClient;
        }

    }

    private void init() {
        init(null);
    }

    public DownloadManager(OkHttpClient client, Context context) {
        this.client = client;
        this.context = context;
        init(client);
    }

    private DownloadManager() {
        init();
    }

    private DownloadManager(Context context) {
        this.context = context;
        init(null);
    }

    public static DownloadManager getInstance(Context context) {
        if (downloadManager == null) {
            downloadManager = new DownloadManager(context);
        }
        return downloadManager;
    }

    public static DownloadManager getInstance(OkHttpClient okHttpClient, Context context) {
        if (downloadManager == null) {
            downloadManager = new DownloadManager(okHttpClient, context);
        }
        return downloadManager;
    }

    /**
     添加下载任务
     */
    public DownloadTask addDownloadTask(DownloadTask task, DownloadTaskListener listener) {
        DownloadTask downloadTask = currentTaskList.get(task.getId());
        if (null != downloadTask
                && downloadTask.getDownloadStatus() != HttpTaskStatus.HTTP_STATUS_CANCEL) {
            Log.d(TAG, "task already exist");
            return downloadTask;
        }
        currentTaskList.put(task.getId(), task);
        task.setDownloadStatus(HttpTaskStatus.HTTP_STATUS_PREPARE);
        task.setDownloadDao(downloadDao);
        task.setHttpClient(client);
        task.addDownloadListener(listener);
        if (getDBTaskById(task.getId()) == null) {
            DownloadDBEntity dbEntity = new DownloadDBEntity(task.getId(), task.getTotalSize(),
                    task.getCompletedSize(), task.getUrl(), task.getSaveDirPath(),
                    task.getFileName(), task.getDownloadStatus());
            downloadDao.insertOrReplace(dbEntity);
        }
        Future future = executorService.submit(task);
        futureMap.put(task.getId(), future);

        return null;
    }

    public DownloadTask resume(String taskId) {
        DownloadTask downloadTask = getCurrentTaskById(taskId);
        if (downloadTask != null) {
            if (downloadTask.getDownloadStatus() == HttpTaskStatus.HTTP_STATUS_PAUSE) {
                Future future = executorService.submit(downloadTask);
                futureMap.put(downloadTask.getId(), future);
            }

        } else {
            downloadTask = getDBTaskById(taskId);
            if (downloadTask != null) {
                currentTaskList.put(taskId, downloadTask);
                Future future = executorService.submit(downloadTask);
                futureMap.put(downloadTask.getId(), future);
            }
        }
        return downloadTask;
    }

    public void addDownloadListener(DownloadTask task, DownloadTaskListener listener) {
        task.addDownloadListener(listener);
    }

    public void removeDownloadListener(DownloadTask task, DownloadTaskListener listener) {
        task.removeDownloadListener(listener);
    }

    //    public DownloadTask addDownloadTask(DownloadTask task) {
    //        return addDownloadTask(task, null);
    //    }

    public void cancel(DownloadTask task) {
        task.cancel();
        currentTaskList.remove(task.getId());
        futureMap.remove(task.getId());
        task.setDownloadStatus(HttpTaskStatus.HTTP_STATUS_CANCEL);
        downloadDao.deleteByKey(task.getId());
    }

    public void cancel(String taskId) {
        DownloadTask task = getTaskById(taskId);
        if (task != null) {
            cancel(task);
        }
    }

    public void pause(DownloadTask task) {
        task.setDownloadStatus(HttpTaskStatus.HTTP_STATUS_PAUSE);
    }

    public void pause(String taskId) {
        DownloadTask task = getTaskById(taskId);
        if (task != null) {
            pause(task);
        }
    }

    public List<DownloadDBEntity> loadAllDownloadEntityFromDB() {
        return downloadDao.loadAll();
    }

    public List<DownloadTask> loadAllDownloadTaskFromDB() {
        List<DownloadDBEntity> list = loadAllDownloadEntityFromDB();
        List<DownloadTask> downloadTaskList = null;
        if (list != null && !list.isEmpty()) {
            downloadTaskList = new ArrayList<>();
            for (DownloadDBEntity entity : list) {
                downloadTaskList.add(DownloadTask.parse(entity));
            }
        }
        return downloadTaskList;
    }

    public List<DownloadTask> loadAllTask() {
        List<DownloadTask> list = loadAllDownloadTaskFromDB();
        Map<String, DownloadTask> currentTaskMap = getCurrentTaskList();
        List<DownloadTask> currentList = new ArrayList<>();
        if (currentTaskMap != null) {
            currentList.addAll(currentTaskMap.values());
        }
        if (!currentList.isEmpty() && list != null) {
            for (DownloadTask task : list) {
                if (!currentList.contains(task)) {
                    currentList.add(task);
                }
            }
        } else {
            if (list != null)
                currentList.addAll(list);
        }
        return currentList;
    }

    public DownloadTask getCurrentTaskById(String taskId) {
        return currentTaskList.get(taskId);
    }

    public DownloadTask getTaskById(String taskId) {
        DownloadTask task = null;
        task = getCurrentTaskById(taskId);
        if (task != null) {
            return task;
        }
        return getDBTaskById(taskId);
    }

    /**
    从数据库获取任务
     */
    public DownloadTask getDBTaskById(String taskId) {
        DownloadDBEntity entity = downloadDao.load(taskId);
        if (entity != null) {
            return DownloadTask.parse(entity);
        }
        return null;
    }

}
