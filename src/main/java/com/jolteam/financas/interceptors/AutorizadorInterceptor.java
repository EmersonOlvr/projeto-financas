package com.jolteam.financas.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.jolteam.financas.model.Usuario;

public class AutorizadorInterceptor implements HandlerInterceptor {

	private final boolean CONTROLAR_ACESSO = true;
	
	private final String[] PAGINAS_ESTATICAS = {"/css/", "/js/", "/img/", "/fonts/", "/util/"};
	private final String[] PAGINAS_DESLOGADO = {"/", "/cadastrar", "/entrar", "/teste",  
												"/ativarConta", "/reenviar-link-ativacao", "/ativacao-conta", 
												"/recuperar-senha", "/recuperar-senha/2", "/redefinir-senha"};
	private final String[] PAGINAS_LOGADO = {"/home", "/configuracoes", "/sair", "/movimentos", 
											 "/receitas/adicionar", "/receitas/historico", 
											 "/receitas/categorias", "/receitas/categorias/excluir", 
											 "/despesas/adicionar", "/despesas/historico", 
											 "/despesas/categorias", "/despesas/categorias/excluir", 
											 "/cofres", "/cofres/editar", "/cofres/cadastrar","/cofres/excluir"};
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception 
	{
		String urlRequisitada = request.getServletPath();
		Usuario usuarioLogado = (Usuario) request.getSession().getAttribute("usuarioLogado");
		boolean estaLogado = usuarioLogado != null ? true : false;
		
		if (!CONTROLAR_ACESSO) {
			return false;
		}
		
		for (String paginaLogado : PAGINAS_LOGADO) {
			if (urlRequisitada.equals(paginaLogado)) {
				if (estaLogado) {
					//System.out.println("Permitido (logado): "+urlRequisitada);
					return true;
				} else {
					//System.out.println("Negado (deslogado): "+urlRequisitada);
					if (!urlRequisitada.equals("/home") && !urlRequisitada.equals("/sair")) {
						response.sendRedirect("/entrar?destino="+urlRequisitada);
					} else {
						response.sendRedirect("/entrar");
					}
					return false;
				}
			}
		}
		for (String paginaDeslogado : PAGINAS_DESLOGADO) {
			if (urlRequisitada.equals(paginaDeslogado)) {
				if (!estaLogado) {
					//System.out.println("Permitido (deslogado): "+urlRequisitada);
					return true;
				} else {
					//System.out.println("Negado (logado): "+urlRequisitada);
					response.sendRedirect("/home");
					return false;
				}
			}
		}
		for (String paginaEstatica : PAGINAS_ESTATICAS) {
			if (urlRequisitada.contains(paginaEstatica)) {
				//System.out.println("Permitido (estática): "+urlRequisitada);
				return true;
			}
		}
		
		//System.out.println("Negado: "+urlRequisitada);
		response.sendRedirect("/");
		return false;
	}
	
}
