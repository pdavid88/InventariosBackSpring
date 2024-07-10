package com.inventarios.repositorio;

import com.inventarios.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductoRepositorio extends JpaRepository<Producto, Integer> {
}
