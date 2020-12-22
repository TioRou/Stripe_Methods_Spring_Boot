package com.example.stripe.DAO;

import com.example.stripe.model.Usuario;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface IUsuarioDao extends CrudRepository<Usuario, Long>{

}
