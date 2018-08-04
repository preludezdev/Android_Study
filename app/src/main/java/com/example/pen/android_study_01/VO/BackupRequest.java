package com.example.pen.android_study_01.VO;

import com.example.pen.android_study_01.Memo;

import java.util.List;

public class BackupRequest {
    private String token;
    private List<Memo> memos;

    public BackupRequest(String token, List<Memo> memos) {
        this.token = token;
        this.memos = memos;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Memo> getMemos() {
        return memos;
    }

    public void setMemos(List<Memo> memos) {
        this.memos = memos;
    }
}
