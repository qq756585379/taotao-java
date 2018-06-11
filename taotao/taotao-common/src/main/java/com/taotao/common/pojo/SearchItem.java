package com.taotao.common.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchItem implements Serializable {

    private String id;
    private String title;
    private String sell_point;
    private long price;
    private String image;
    private String category_name;
    private String item_desc;
    
}
