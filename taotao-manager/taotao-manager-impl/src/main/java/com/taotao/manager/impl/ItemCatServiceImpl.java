package com.taotao.manager.impl;

import com.taotao.common.vo.EasyUITreeNode;
import com.taotao.manager.api.ItemCatService;
import com.taotao.manager.dao.TbItemCatMapper;
import com.taotao.manager.pojo.TbItemCat;
import com.taotao.manager.pojo.TbItemCatExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类管理Service
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper itemCatMapper;

    public List<EasyUITreeNode> getItemCatList(long parentId) {
        //根据父节点id查询子节点列表
        TbItemCatExample example = new TbItemCatExample();
        //设置查询条件
        TbItemCatExample.Criteria criteria = example.createCriteria();
        //设置parentid
        criteria.andParentIdEqualTo(parentId);
        //执行查询
        List<TbItemCat> list = itemCatMapper.selectByExample(example);
        //转换成EasyUITreeNode列表
        List<EasyUITreeNode> resultList = new ArrayList<EasyUITreeNode>();
        for (TbItemCat tbItemCat : list) {
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(tbItemCat.getId());
            node.setText(tbItemCat.getName());
            //如果节点下有子节点“closed”，如果没有子节点“open”
            node.setState(tbItemCat.getIsParent() ? "closed" : "open");
            //添加到节点列表
            resultList.add(node);
        }
        return resultList;
    }
}

