//var googleUser = {};
//window.onload = function() {
//	gapi.load('auth2', function(){
//		// Retrieve the singleton for the GoogleAuth library and set up the client.
//		auth2 = gapi.auth2.init({
//			client_id: '238270162782-te8br3159e2d8bjngh895bb2r3mmt6pb.apps.googleusercontent.com',
//			cookiepolicy: 'single_host_origin',
//			// Request scopes in addition to 'profile' and 'email'
//			//scope: 'additional_scope'
//		});
//		attachSignin(document.getElementById('customBtn'));
//	});
//};
//
//function attachSignin(element) {
//	auth2.attachClickHandler(
//		element, {},
//		function(googleUser) {
//			usuario = googleUser.getBasicProfile();
//			
//			console.log("getId(): " + usuario.getId());
//			console.log("getName(): " + usuario.getName());
//			console.log("getGivenName(): " + usuario.getGivenName());
//			console.log("getFamilyName(): " + usuario.getFamilyName());
//			console.log("getImageUrl(): " + usuario.getImageUrl());
//			console.log("getEmail(): " + usuario.getEmail());
//			
//			alert("Logado: "+usuario.getName());
//		}, function(error) {
//			//alert(JSON.stringify(error, undefined, 2));
//			alert("Erro ao entrar com o Google!");
//		}
//	);
//}