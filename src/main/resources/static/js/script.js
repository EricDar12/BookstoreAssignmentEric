function verify() {
	var password1 = document.forms['form']['password'].value;
	var password2 = document.forms['form']['verifyPassword'].value;
	var username = document.forms['form']['username'].value;
	
	if (password1 == null || password1 == "" || password1 != password2) {
		document.getElementById("error").innerHTML = "Please check your passwords";
		return false;
	}
	else if (username.length < 7) {
		document.getElementById("error").innerHTML = "Username must be 7 characters";
		return false;
	}
}