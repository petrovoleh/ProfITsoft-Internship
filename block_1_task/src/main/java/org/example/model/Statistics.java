package org.example.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;
import java.util.Map;

/**
 * Цей клас представляє модель статистики замовлень для формату XML.
 */
@JsonRootName(value = "статистика")
public class Statistics {
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Map<String, Object>> item;

    /**
     * Створює об'єкт статистики замовлень.
     * @param item список елементів статистики
     */
    public Statistics(List<Map<String, Object>> item) {
        this.item = item;
    }


    /* Гетери та сетери */
    public List<Map<String, Object>> getItems() {
        return item;
    }

    public void setItems(List<Map<String, Object>> item) {
        this.item = item;
    }
}


