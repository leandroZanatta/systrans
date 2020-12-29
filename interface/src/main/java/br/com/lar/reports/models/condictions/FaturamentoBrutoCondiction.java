package br.com.lar.reports.models.condictions;

import java.util.Map;

import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionStyleExpression;

public class FaturamentoBrutoCondiction extends ConditionStyleExpression implements CustomExpression {

	private static final long serialVersionUID = 1L;

	private final Integer condiction;

	public FaturamentoBrutoCondiction(Integer condiction) {
		this.condiction = condiction;
	}

	@Override
	public Object evaluate(Map fields, Map variables, Map parameters) {

		Integer compare = (Integer) fields.get("agrupamento");

		return condiction.equals(compare);
	}

	@Override
	public String getClassName() {

		return Boolean.class.getName();
	}

}