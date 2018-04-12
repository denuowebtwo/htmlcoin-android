package org.qtum.wallet.ui.fragment.backup_contracts_fragment;


import org.qtum.wallet.R;
import org.qtum.wallet.ui.base.base_fragment.BaseFragment;
import org.qtum.wallet.ui.base.base_fragment.BaseFragmentPresenterImpl;
import org.qtum.wallet.utils.LogUtils;

import java.io.File;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;


public class BackupContractsPresenterImpl extends BaseFragmentPresenterImpl implements BackupContractsPresenter {

    private BackupContractsView mBackupContractsFragmentView;
    private BackupContractsInteractor mBackupContractsInteractor;

    public BackupContractsPresenterImpl(BackupContractsView backupContractsFragmentView, BackupContractsInteractor backupContractsInteractor) {
        mBackupContractsFragmentView = backupContractsFragmentView;
        mBackupContractsInteractor = backupContractsInteractor;
    }

    private File mBackUpFile;

    @Override
    public BackupContractsView getView() {
        return mBackupContractsFragmentView;
    }

    @Override
    public void onBackUpClick() {
        getView().checkPermissionForBackupFile();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBackUpFile != null) {
            mBackUpFile.delete();
        }
    }

    public void permissionGrantedForCreateBackUpFile() {
        getView().setProgressDialog();
        getInteractor().createBackUpFile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().setAlertDialog(R.string.error,R.string.cancel, BaseFragment.PopUpType.error);
                    }

                    @Override
                    public void onNext(File file) {
                        String backUpFileSize = String.valueOf((int) Math.ceil(file.length() / 1024.0)) + " Kb";
                        getView().dismissProgressDialog();
                        getView().setUpFile(backUpFileSize);
                        mBackUpFile = file;
                    }
                });

    }

    public void permissionGrantedForCreateAndBackUpFile() {
        getView().setProgressDialog();
        getInteractor().createBackUpFile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().setAlertDialog(R.string.error,R.string.cancel, BaseFragment.PopUpType.error);
                    }

                    @Override
                    public void onNext(File file) {
                        String backUpFileSize = String.valueOf((int) Math.ceil(file.length() / 1024.0)) + " Kb";
                        getView().dismissProgressDialog();
                        getView().setUpFile(backUpFileSize);
                        mBackUpFile = file;
                        getView().checkPermissionForBackupFile();
                    }
                });

    }

    @Override
    public void permissionGrantedForChooseShareMethod() {
        try {
            if (mBackUpFile.exists()) {
                getView().chooseShareMethod(mBackUpFile.getAbsolutePath());
            } else {
                getView().setAlertDialog(R.string.error, R.string.cancel, BaseFragment.PopUpType.error);
            }
        } catch (Exception ex) {
            getView().setAlertDialog(R.string.error, R.string.cancel, BaseFragment.PopUpType.error);
            LogUtils.error("Backup", ex.getMessage(), ex);
        }
    }

    //setter for unit testing
    public void setBackUpFile(File backUpFile) {
        mBackUpFile = backUpFile;
    }

    private BackupContractsInteractor getInteractor() {
        return mBackupContractsInteractor;
    }

}