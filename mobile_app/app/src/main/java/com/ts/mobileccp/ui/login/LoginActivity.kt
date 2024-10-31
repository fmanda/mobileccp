package com.ts.mobileccp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ts.mobileccp.MainActivity
import com.ts.mobileccp.databinding.ActivityLoginBinding
import com.ts.mobileccp.global.AppVariable
import androidx.fragment.app.DialogFragment


class LoginActivity : AppCompatActivity(), DialogLoginAdvSetting.DialogLoginSettingListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = LoginViewModelFactory(this.application)
        loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
//            binding.pgBar.visibility = View.VISIBLE
            val username = binding.txtUserName.text.toString()
            val password = binding.txtPassword.text.toString()

            loginViewModel.login(username, password)
        }

        loginViewModel.isLogin.observe(this, Observer { isLogin ->
            if (isLogin == true) {


                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else if (isLogin == false) {
//                binding.pgBar.visibility = View.GONE
                // User is not logged in
            } else {
//                binding.pgBar.visibility = View.GONE
                // Login status is unknown (null)
            }
        })

        loginViewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading == true) {
                binding.pgBar.visibility = View.VISIBLE
                binding.btnLogin.visibility = View.GONE
            } else {
                binding.pgBar.visibility = View.GONE
                binding.btnLogin.visibility = View.VISIBLE
            }
        })

        binding.txtAPI.setOnClickListener{
            DialogLoginAdvSetting(AppVariable.setting.api_url, this )
                .show(supportFragmentManager, "DialogLoginAdvSetting")
        }

    }

    override fun onTestAPI(api: String) {
        loginViewModel.testApi(api)
    }

    override fun onUpdateAPI(api: String) {
        loginViewModel.updateApi(api)
    }

}

