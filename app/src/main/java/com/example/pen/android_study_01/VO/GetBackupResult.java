package com.example.pen.android_study_01.VO;

import com.example.pen.android_study_01.Memo;

import java.util.List;

public class GetBackupResult {
    private List<Memo> memos;

    public GetBackupResult(List<Memo> memos) {
        this.memos = memos;
    }

    public List<Memo> getMemos() {
        return memos;
    }

    public void setMemos(List<Memo> memos) {
        this.memos = memos;
    }
}
