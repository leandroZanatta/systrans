package br.com.lar.repository.interfaces;

import java.util.List;

import com.mysema.query.BooleanBuilder;

import br.com.sysdesc.pesquisa.repository.model.Pesquisa;

public interface GenericDAO<T> {

	public abstract T previows(Long id);

	public abstract T next(Long id);

	public abstract T last();

	public abstract T first();

	public abstract void salvar(T objetoPesquisa);

	public abstract List<T> pesquisar(boolean selected, String text, BooleanBuilder preFilter, Pesquisa pesquisaExibir,
			Integer rows);

	public abstract Long count(boolean selected, String pesquisa, BooleanBuilder preFilter, Pesquisa pesquisaExibir);

}