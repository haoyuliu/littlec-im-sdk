package com.littlec.sdk.biz.chat.entity.body;

import android.os.Parcel;
import android.os.Parcelable;

import com.littlec.sdk.biz.chat.entity.LCMessage;

/**
 * Created by user on 2017/3/1.
 */

public class LCJiMaoMessageBody extends LCMessageBody {
    private String from_user_id;
    private String read_user_id;
    private String read_nick;
    private long guid;
    private String uri;

    public LCJiMaoMessageBody() {
    }


    public String getRead_user_id() {
        return read_user_id;
    }

    public void setRead_user_id(String read_user_id) {
        this.read_user_id = read_user_id;
    }

    public String getRead_nick() {
        return read_nick;
    }

    public void setRead_nick(String read_nick) {
        this.read_nick = read_nick;
    }

    public long getGuid() {
        return guid;
    }

    public void setGuid(long guid) {
        this.guid = guid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFrom_user_id() {

        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(super.contentType == null ? -1 : this.contentType.value());
        parcel.writeString(this.from_user_id);
        parcel.writeString(this.read_user_id);
        parcel.writeString(this.read_nick);
        parcel.writeLong(this.guid);
        parcel.writeString(this.uri);
    }

    private LCJiMaoMessageBody(Parcel in) {
        int tmpContentType = in.readInt();
        super.contentType = tmpContentType == -1 ? null : LCMessage.ContentType.values()[tmpContentType];
        this.from_user_id = in.readString();
        this.read_user_id = in.readString();
        this.read_nick = in.readString();
        this.guid = in.readLong();
        this.uri = in.readString();
    }

    public static final Parcelable.Creator<LCJiMaoMessageBody> CREATOR = new Parcelable.Creator<LCJiMaoMessageBody>() {
        public LCJiMaoMessageBody createFromParcel(Parcel in) {
            return new LCJiMaoMessageBody(in);
        }

        public LCJiMaoMessageBody[] newArray(int size) {
            return new LCJiMaoMessageBody[size];
        }
    };
}
