package br.com.lar.util.constants;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.lar.atualizacao.changelog.core.Conexao;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.constants.MensagemConstants;
import br.com.sysdesc.util.resources.Resources;

@RunWith(MockitoJUnitRunner.class)
public class ResourcesTest {

    @Test
    public void testOrdenarPtBr() throws Exception {

        List<String> resources = FileUtils.readLines(new File("../interface/resources/pt_br.properties"), Charset.forName("UTF-8")).stream()
                .filter(x -> x != null && !"".equals(x)).sorted(Comparator.comparing(String::toString)).collect(Collectors.toList());

        List<String> newList = new ArrayList<String>();

        LinkedHashMap<String, List<String>> result = resources.stream().collect(Collectors.groupingBy(a -> a.toString().split("_")[0])).entrySet()
                .stream().sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        for (Entry<String, List<String>> keyset : result.entrySet()) {
            newList.addAll(keyset.getValue().stream().sorted(Comparator.comparing(String::toString)).collect(Collectors.toList()));
            newList.add("");
        }

        FileUtils.writeLines(new File("../interface/resources/pt_br.properties"), "UTF-8", newList);
    }

    @Test
    public void testPtBrsemResource() throws Exception {

        Properties properties = getPropertiesPtBr();

        Set<String> resources = new HashSet<>();

        resources.addAll(getPropertiesFromDb());

        resources.addAll(getPropertiesFromMensagens());

        resources.addAll(getPropertiesFromResources());

        Boolean testeSucesso = Boolean.TRUE;

        List<String> mensagensExcluir = new ArrayList<>();

        for (Entry<Object, Object> entry : properties.entrySet()) {

            if (!resources.contains(entry.getKey().toString())) {

                System.out.println("Excluir Chave do arquivo pt_br.properties: " + entry.getKey().toString());

                mensagensExcluir.add(entry.getKey().toString());

                testeSucesso = Boolean.FALSE;
            }
        }

        if (!testeSucesso) {

            mensagensExcluir.forEach(x -> properties.remove(x));

            properties.store(new FileOutputStream(new File("../interface/resources/pt_br.properties")), "");
        }

        assertTrue(testeSucesso);
    }

    @Test
    public void testResourcesMensagensNaoCadastradas() throws Exception {

        Properties properties = getPropertiesPtBr();

        List<String> resourcesFromMensagens = getPropertiesFromMensagens();

        Boolean testeSucesso = Boolean.TRUE;

        for (String field : resourcesFromMensagens) {

            if (properties.getProperty(field, "").equals("")) {

                System.out.println("Mensagem não encontrada no arquivo pt_br.properties: " + field);

                testeSucesso = Boolean.FALSE;
            }
        }

        assertTrue(testeSucesso);
    }

    @Test
    public void testResourcesNaoCadastradasDb() throws Exception {

        Properties properties = getPropertiesPtBr();

        List<String> resourcesdb = getPropertiesFromDb();

        Boolean testeSucesso = Boolean.TRUE;

        for (String resource : resourcesdb) {

            if (properties.getProperty(resource, "").equals("")) {

                System.out.println("Programa não encontrado no arquivo pt_br.properties: " + resource);

                testeSucesso = Boolean.FALSE;
            }
        }

        assertTrue(testeSucesso);
    }

    @Test
    public void testResourcesNaoCadastradas() throws Exception {

        Properties properties = getPropertiesPtBr();

        List<String> resourcesFromMensagens = getPropertiesFromResources();

        Boolean testeSucesso = Boolean.TRUE;

        for (String field : resourcesFromMensagens) {

            if (properties.getProperty(field, "").equals("")) {

                System.out.println("Mensagem não encontrada no arquivo pt_br.properties: " + field);

                testeSucesso = Boolean.FALSE;
            }
        }

        assertTrue(testeSucesso);
    }

    @Test
    public void testResourcesDuplicadas() throws Exception {

        List<String> valores = FileUtils.readLines(new File("../interface/resources/pt_br.properties"), Charset.forName("UTF-8"));

        Map<String, List<String>> mapa = valores.stream().map(x -> x.split("=")[0]).filter(x -> !"".equals(x))
                .collect(Collectors.groupingBy(String::toString));

        Boolean testeSucesso = Boolean.TRUE;

        for (Entry<String, List<String>> x : mapa.entrySet()) {

            if (x.getValue().size() > 1) {

                System.out.println("Chaves duplicadas no arquivo pt_br.properties :" + x.getKey());

                testeSucesso = Boolean.FALSE;
            }
        }

        assertTrue(testeSucesso);
    }

    private List<String> getPropertiesFromMensagens() {

        Class<?> clazz = MensagemConstants.class;

        return ListUtil.toList(clazz.getDeclaredFields()).stream().map(Field::getName).collect(Collectors.toList());

    }

    private List<String> getPropertiesFromResources() {

        Class<?> clazz = Resources.class;

        return ListUtil.toList(clazz.getDeclaredFields()).stream().filter(x -> x.getModifiers() != 10 && !x.getName().equals("log"))
                .map(Field::getName).collect(Collectors.toList());
    }

    private List<String> getPropertiesFromDb() throws Exception {

        String sql = "select tx_descricao from tb_programa";

        List<String> resourcesdb = new ArrayList<String>();

        try (Connection connection = Conexao.buscarConexao();
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                resourcesdb.add(rs.getString(1));
            }
        }

        return resourcesdb;
    }

    private Properties getPropertiesPtBr() throws FileNotFoundException, IOException {

        Properties properties = new Properties();

        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File("../interface/resources/pt_br.properties")),
                Charset.forName("UTF-8"));

        properties.load(inputStreamReader);

        return properties;
    }

}
