package com.taotao.manager.api;

import com.taotao.common.vo.EasyUITreeNode;

import java.util.List;

public interface ItemCatService {

    List<EasyUITreeNode> getItemCatList(long parentId);

}
