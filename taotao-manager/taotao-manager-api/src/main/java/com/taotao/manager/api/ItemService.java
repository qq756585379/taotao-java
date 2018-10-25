package com.taotao.manager.api;

import com.taotao.common.vo.EasyUIDataGridResult;
import com.taotao.common.vo.TaotaoResult;
import com.taotao.manager.pojo.TbItem;
import com.taotao.manager.pojo.TbItemDesc;

public interface ItemService {

    TbItem getItemById(long itemId);

    EasyUIDataGridResult getItemList(int page, int rows);

    TaotaoResult addItem(TbItem item, String desc);

    TbItemDesc getItemDescById(long itemId);

}
