package br.com.systrans.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RateioUtil {

	private RateioUtil() {

	}

	public static <T> void efetuarRateio(List<T> lista, Function<T, BigDecimal> funcaoGet, BiConsumer<T, BigDecimal> funcaoSet,
			BigDecimal valorTotal) {

		BigDecimal valorAcumulado = BigDecimal.ZERO;

		for (int i = 0; i < lista.size(); i++) {

			if (i == lista.size() - 1) {

				funcaoSet.accept(lista.get(i), valorTotal.subtract(valorAcumulado));

				continue;
			}

			valorAcumulado = valorAcumulado.add(funcaoGet.apply(lista.get(i)));
		}
	}
}
