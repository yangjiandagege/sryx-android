package com.yj.sryx.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yj.sryx.R;
import com.yj.sryx.manager.StatusBarUtil;
import com.yj.sryx.manager.qrcode.core.QRCodeView;
import com.yj.sryx.manager.qrcode.zxing.QRCodeDecoder;
import com.yj.sryx.manager.qrcode.zxing.ZXingView;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class QrCodeScanActivity extends BaseActivity implements QRCodeView.Delegate {
    private static final String TAG = QrCodeScanActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    public static final String IS_BARCODE = "is_barcode";
    public static final String QR_SCAN_RESULT = "qr_scan_result";
    private QRCodeView mQRCodeView;
    private boolean isFlashlightOpen = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        ButterKnife.bind(this);
        StatusBarUtil.StatusBarLightMode(this);
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getBoolean(IS_BARCODE, false)) {
                mQRCodeView.changeToScanBarcodeStyle();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();
        Intent intent = new Intent();
        intent.putExtra(QR_SCAN_RESULT, result);
        setResult(RESULT_OK, intent);
        finish();
        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mQRCodeView.showScanRect();

        if (requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY && resultCode == Activity.RESULT_OK && null != data) {
            String picturePath;
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                picturePath = c.getString(columnIndex);
                c.close();
            } catch (Exception e) {
                picturePath = data.getData().getPath();
            }

            if (new File(picturePath).exists()) {
                QRCodeDecoder.decodeQRCode(BitmapFactory.decodeFile(picturePath), new QRCodeDecoder.Delegate() {
                    @Override
                    public void onDecodeQRCodeSuccess(String result) {
                        Toast.makeText(QrCodeScanActivity.this, result, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDecodeQRCodeFailure() {
                        Toast.makeText(QrCodeScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @OnClick({R.id.open_flashlight, R.id.choose_qrcde_from_gallery})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_flashlight:
                if (isFlashlightOpen) {
                    mQRCodeView.closeFlashlight();
                    ((TextView) view).setText("开灯");
                    isFlashlightOpen = false;
                } else {
                    mQRCodeView.openFlashlight();
                    ((TextView) view).setText("关灯");
                    isFlashlightOpen = true;
                }
                break;
            case R.id.choose_qrcde_from_gallery:
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                        REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
                break;
        }
    }
}