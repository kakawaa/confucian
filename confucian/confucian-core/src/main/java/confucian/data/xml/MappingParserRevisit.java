package confucian.data.xml;

import com.google.common.collect.Maps;
import confucian.common.Utils;
import confucian.data.IDataSource;
import confucian.data.IMappingData;
import confucian.data.ImplementIMap;
import confucian.data.PropertyValueMin;
import confucian.driver.SuiteConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping.xml 解析器
 */
public class MappingParserRevisit implements IDataSource {
    private static final String DELIMITER = ";";
    private static final Logger LOGGER = LogManager.getLogger();
    private HashMap<String, IMappingData> bucket = Maps.newHashMap();
    private Document document = null;

    /**
     * 实例化一个新的映射解析器
     */
    public MappingParserRevisit() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(Utils.getResources(getMappingFile()));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * 获取映射文件
     *
     * @return 映射文件
     */
    private static String getMappingFile() {
        if (StringUtils.isBlank(getCalcValue("mappingfile"))) {
            return "Mapping.xml";
        }
        return getCalcValue("mappingfile");
    }

    private static String getCalcValue(String key) {
        if (StringUtils.isNotBlank(System.getProperty(key))) {
            return System.getProperty(key);
        } else {
            return getFrameworkPropertyValue(key);
        }
    }

    private static String getFrameworkPropertyValue(String key) {
        PropertyValueMin prop;
        if (isFrameworkProperties()) {
            prop = new PropertyValueMin(Utils.getResources("Framework.properties"));
            return prop.getValue(key);
        } else {
            return "";
        }
    }

    /**
     * 框架属性文件是否存在：Framework.properties
     *
     * @return true or false
     */
    private static boolean isFrameworkProperties() {
        return Utils.getResources("Framework.properties") != null;
    }

    /**
     * 获取版本号
     *
     * @return 内部版本号 build number
     */
    public static String getBuildNumber() {
        return getCalcValue("buildNumber");
    }

    /**
     * 获取项目名称
     *
     * @return 项目名称 project name
     */
    public static String getProjectName() {
        if (StringUtils.isNotBlank(getCalcValue("projectName"))) {
            return getCalcValue("projectName");
        } else {
            return SuiteConfiguration.suiteName;
        }
    }

    /**
     * 获取{@link IMappingData} 从任何输入的数据源
     *
     * @param element element
     * @return IMappingData
     */
    private IMappingData getMapping(Element element) {
        return new ImplementIMap.Builder().withRunStrategy(element.getAttribute("runStrategy"))
                .withTestData(element.getAttribute("testData"))
                .withClientEnvironment(Utils.getSplitList(element.getAttribute("clientEnvironment"), DELIMITER))
                .withDataProviderPath(element.getAttribute("testData")).build();
    }

    /**
     * 主数据，{@link IMappingData}
     */
    public Map<String, IMappingData> getPrimaryData() {
        // 获取根元素
        walkInXml(document.getDocumentElement());
        for (String bs_key : bucket.keySet()) {
            LOGGER.info("测试类名称: " + bs_key);
            LOGGER.info(" 测试数据: " + bucket.get(bs_key).getTestData());
            LOGGER.info(" 客户端环境: " + bucket.get(bs_key).getClientEnvironment());
            LOGGER.info(" 运行策略: " + bucket.get(bs_key).getRunStrategy());
        }
        return bucket;
    }

    @Override
    public String toString() {
        String xmlName = "";
        return "读取Xml文件的名称:" + xmlName;
    }

    /**
     * 更新主桶存储值
     *
     * @param element Element
     */
    private void updateBucket(Element element) {
        LOGGER.debug("测试类名称: " + element.getAttribute("name"));
        bucket.put(element.getAttribute("name"), getMapping(element));
    }

    /**
     * 读取Xml更新映射中的每一个元素与值
     *
     * @param element Element
     */
    private void walkInXml(Element element) {
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n instanceof Element) {
                Element childElement = (Element) n;
                updateBucket(childElement);
                walkInXml(childElement);
            }
        }
    }
}