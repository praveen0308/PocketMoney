package com.sampurna.pocketmoney.utils;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class CustomValidator {

    private Activity activity;

    public CustomValidator(Activity activity) {
        this.activity = activity;

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public boolean validatePassword(TextInputLayout inputLayout, TextInputEditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            inputLayout.setError("Password is required");
            requestFocus(editText);
            return false;
        } else if (editText.getText().toString().length() < 4) {
            inputLayout.setError("Password can't be less than 4 digit");
            requestFocus(editText);
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }


    public boolean validateConfirmPassword(TextInputLayout inputLayout, TextInputEditText etConfirmPassword, TextInputEditText etNewPassword) {
        if (etConfirmPassword.getText().toString().trim().isEmpty()) {
            inputLayout.setError("Confirm password is required");
            requestFocus(etConfirmPassword);
            return false;
        } else if (!etConfirmPassword.getText().toString().equals(etNewPassword.getText().toString())) {
            inputLayout.setError("Confirm Password is not matching");
            requestFocus(etConfirmPassword);
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validateName(TextInputLayout inputLayout, TextInputEditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            inputLayout.setError("This field cannot be empty!!!");
//            requestFocus(editText);
            editText.requestFocus();
            return false;
        } else if (!editText.getText().toString().matches("[a-zA-Z ]+")) {
            inputLayout.setError("Enter only alphabetical character!!!");
//            requestFocus(editText);
            editText.requestFocus();
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
            return true;
        }

    }

    public boolean checkForEmpty(TextInputLayout inputLayout, TextInputEditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            inputLayout.setError("This field cannot be empty!!!");
//            requestFocus(editText);
            editText.requestFocus();
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validateName(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError("This field cannot be empty!!!");
            editText.requestFocus();
            return false;
        } else if (!editText.getText().toString().matches("[a-zA-Z ]+")) {
            editText.setError("Enter only alphabetical character!!!");
            editText.requestFocus();
            return false;
        } else {
//            editText.setError("");

        }
        return true;
    }





    public boolean validateMobileNo(TextInputLayout inputLayout, TextInputEditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            inputLayout.setError("Enter mobile number!!!");
            editText.requestFocus();
            return false;
        } else if (!editText.getText().toString().matches("^[0-9]{2}[0-9]{8}$")) {
            inputLayout.setError("Enter valid mobile number !!!");
            editText.requestFocus();
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }
    public boolean validatePincode(TextInputLayout inputLayout, TextInputEditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            inputLayout.setError("Enter pincode !!!");
            editText.requestFocus();
            return false;
        } else if (editText.getText().toString().length()<6) {
            inputLayout.setError("Enter valid pincode number !!!");
            editText.requestFocus();
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validateAlternateMobileNo(TextInputLayout inputLayout, TextInputEditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            inputLayout.setErrorEnabled(false);
        }else if (!editText.getText().toString().matches("^[0-9]{2}[0-9]{8}$")) {
            inputLayout.setError("Enter valid mobile number !!!");
            editText.requestFocus();
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validateClassId(TextInputLayout inputLayout, TextInputEditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            inputLayout.setError("This field cannot be empty!!!");
            requestFocus(editText);
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }


    public boolean validateEmail(TextInputLayout inputLayout, TextInputEditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            inputLayout.setError("Enter valid email id!!!");
            editText.requestFocus();
            return false;

        } else {
            String emailId = editText.getText().toString();
            boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
            if (!isValid) {
                inputLayout.setError("Invalid Email address, ex: abc@example.com");
                editText.requestFocus();
                return false;
            } else {
                inputLayout.setErrorEnabled(false);
            }
        }
        return true;
    }
}
