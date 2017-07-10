package com.yj.sryx.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by eason.yang on 2017/7/10.
 */
public class LoginModelImplTest {

    private LoginModelImpl mLoginModel;

    public LoginModelImplTest() {
        mLoginModel = new LoginModelImpl();
    }

    @Test
    public void wxLogin() throws Exception {

    }

    @Test
    public void isAlreadyLogin() throws Exception {
        assertEquals(false, mLoginModel.isAlreadyLogin());
    }

    @Test
    public void getWxUserInfo() throws Exception {
        mLoginModel.getWxUserInfo("Lervt54-UbCknpKXvYPgYP46kER1En9rnM8CY9yktaliZRFgTf3zWIKQuegOnnGaeHpRhwsYwbzGGg8lDN8Xny1D_RWrTEP38UjgCiYaIgQ"
                , "op1eTwiRFiCEpEm0ZtZMIlHWd0jw", new BeanCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        assertEquals(true, true);
                    }

                    @Override
                    public void onError(String msg) {
                        assertEquals(false, true);
                    }
                });
    }
}