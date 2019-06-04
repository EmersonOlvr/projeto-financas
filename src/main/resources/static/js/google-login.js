var googleUser = {};
window.onload = function() {
	gapi.load('auth2', function(){
		// Retrieve the singleton for the GoogleAuth library and set up the client.
		auth2 = gapi.auth2.init({
			client_id: '238270162782-te8br3159e2d8bjngh895bb2r3mmt6pb.apps.googleusercontent.com',
			cookiepolicy: 'single_host_origin',
			// Request scopes in addition to 'profile' and 'email'
			//scope: 'additional_scope'
		});
		attachSignin(document.getElementById('customBtn'));
	});
};

function attachSignin(element) {
	auth2.attachClickHandler(
		element, {},
		function(googleUser) {
			/*var customBtn = document.getElementById("customBtn");
			customBtn.parentNode.removeChild(customBtn);
			var btnSair = document.getElementById("btnSair");
			btnSair.innerHTML = "<button onclick=\"signOut()\">Sair</button>";

			googleUser = googleUser.getBasicProfile();
			console.log('Logged in as: ' + googleUser.getName());

			document.getElementById("uFoto").src = googleUser.getImageUrl();
			document.getElementById("uNomeCompleto").innerHTML = "<b>Nome completo:</b> "+googleUser.getName();
			document.getElementById("uNome").innerHTML = "<b>Nome:</b> "+googleUser.getGivenName();
			document.getElementById("uEmail").innerHTML = "<b>Email:</b> "+googleUser.getEmail();*/
			alert("Logado: "+googleUser.getBasicProfile().getName());
		}, function(error) {
			//alert(JSON.stringify(error, undefined, 2));
			alert("Erro ao entrar com o Google!");
		}
	);
}