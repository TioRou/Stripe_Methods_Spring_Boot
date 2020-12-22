package com.example.stripe.service;

import com.example.stripe.model.Usuario;

public interface IUsuarioService {

	public Usuario save(Usuario u);
	
	public Usuario findById(Long id);
	
	public void delete(Long id);
	
}
