package com.littlec.sdk.database.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class TagEntity {
    @Id
    private long id;
    private String name = "";
    private String img = "";
    private String desc = "";

    @Generated(hash = 1289205753)
    public TagEntity(long id, String name, String img, String desc) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.desc = desc;
    }

    @Generated(hash = 2114918181)
    public TagEntity() {
    }

//    @Keep
//    public TagEntity(Group.GroupTag tag) {
//        this.id = tag.getTagId();
//        this.name = tag.getTagName();
//        this.img = tag.getTagImg();
//        this.desc = tag.getTagDesc();
//    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
