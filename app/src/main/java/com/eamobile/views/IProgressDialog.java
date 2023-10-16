package com.eamobile.views;

public interface IProgressDialog {
    void dismissDialog();

    void initDialog();

    boolean isDialogValid();

    void setDownloadMax(int i);

    void setDownloadProgress(int i);

    void setDownloadSpeed(float f);

    void showDialogContent();

    void updateDialog();
}
