package com.example.pen.android_study_01.VO;

public class ValidResult extends BaseResult {
    private boolean valid;

    public ValidResult(String msg, boolean valid) {
        super(msg);
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
