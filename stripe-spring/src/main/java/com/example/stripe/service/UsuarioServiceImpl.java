package com.example.stripe.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stripe.DAO.IUsuarioDao;
import com.example.stripe.model.Usuario;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

	private Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);
	
	@Autowired
	private IUsuarioDao userDAO;

	@Override
	@Transactional
	public Usuario save(Usuario user) {
		return this.userDAO.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public Usuario findById(Long id) {
		return this.userDAO.findById(id).orElse(null);
	}
	
	@Override
	@Transactional
	public void delete(Long id) {
		this.userDAO.deleteById(id);
	}
	
}
