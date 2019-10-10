package com.littlec.sdk.database.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Entity mapped to table "Member".
 */
@Entity
public class MemberEntity {
    @Id
    private Long id;
    private String memberId;
    private String memberNick;
    private Integer memberShip;
    private int mute;
    private long createDate;
    private long modifyDate;
    private String data1;
    @NotNull
    private String groupId;
    @Generated(hash = 780987141)
    public MemberEntity(Long id, String memberId, String memberNick,
            Integer memberShip, int mute, long createDate, long modifyDate,
            String data1, @NotNull String groupId) {
        this.id = id;
        this.memberId = memberId;
        this.memberNick = memberNick;
        this.memberShip = memberShip;
        this.mute = mute;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.data1 = data1;
        this.groupId = groupId;
    }
    @Generated(hash = 1903663216)
    public MemberEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMemberId() {
        return this.memberId;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    public String getMemberNick() {
        return this.memberNick;
    }
    public void setMemberNick(String memberNick) {
        this.memberNick = memberNick;
    }
    public Integer getMemberShip() {
        return this.memberShip;
    }
    public void setMemberShip(Integer memberShip) {
        this.memberShip = memberShip;
    }
    public int getMute() {
        return this.mute;
    }
    public void setMute(int mute) {
        this.mute = mute;
    }
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
    public long getModifyDate() {
        return this.modifyDate;
    }
    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }
    public String getData1() {
        return this.data1;
    }
    public void setData1(String data1) {
        this.data1 = data1;
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


}
