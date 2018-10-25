package com.taotao.manager.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.jedis.JedisClient;
import com.taotao.common.utils.IDUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.common.vo.EasyUIDataGridResult;
import com.taotao.common.vo.TaotaoResult;
import com.taotao.manager.api.ItemService;
import com.taotao.manager.dao.TbItemDescMapper;
import com.taotao.manager.dao.TbItemMapper;
import com.taotao.manager.pojo.TbItem;
import com.taotao.manager.pojo.TbItemDesc;
import com.taotao.manager.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.jms.*;
import javax.print.attribute.standard.Destination;
import java.util.Date;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemDescMapper itemDescMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Resource(name = "itemAddtopic")
    private Destination destination;

    @Autowired
    private JedisClient jedisClient;

    @Value("${ITEM_INFO}")
    private String ITEM_INFO;
    @Value("${TIEM_EXPIRE}")
    private Integer TIEM_EXPIRE;

    public TbItem getItemById(long itemId) {
        //查询数据库之前先查询缓存
        try {
            String json = jedisClient.get(ITEM_INFO + ":" + itemId + ":BASE");
            if (!StringUtils.isEmpty(json)) {
                // 把json数据转换成pojo
                return JsonUtils.jsonToPojo(json, TbItem.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //缓存中没有查询数据库
        TbItem item = itemMapper.selectByPrimaryKey(itemId);

        try {
            //把查询结果添加到缓存
            jedisClient.set(ITEM_INFO + ":" + itemId + ":BASE", JsonUtils.objectToJson(item));
            //设置过期时间，提高缓存的利用率
            jedisClient.expire(ITEM_INFO + ":" + itemId + ":BASE", TIEM_EXPIRE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    public EasyUIDataGridResult getItemList(int page, int rows) {
        //设置分页信息
        PageHelper.startPage(page, rows);
        //执行查询
        TbItemExample example = new TbItemExample();
        List<TbItem> list = itemMapper.selectByExample(example);
        //取查询结果
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setRows(list);
        result.setTotal(pageInfo.getTotal());
        //返回结果
        return result;
    }

    public TaotaoResult addItem(TbItem item, String desc) {
        //生成商品id
        final long itemId = IDUtils.genItemId();
        //补全item的属性
        item.setId(itemId);
        //商品状态，1-正常，2-下架，3-删除
        item.setStatus((byte) 1);
        item.setCreated(new Date());
        item.setUpdated(new Date());
        //向商品表插入数据
        itemMapper.insert(item);
        //创建一个商品描述表对应的pojo
        TbItemDesc itemDesc = new TbItemDesc();
        //补全pojo的属性
        itemDesc.setItemId(itemId);
        itemDesc.setItemDesc(desc);
        itemDesc.setUpdated(new Date());
        itemDesc.setCreated(new Date());
        //向商品描述表插入数据
        itemDescMapper.insert(itemDesc);

        //向Activemq发送商品添加消息
        // jmsTemplate.send(destination, new MessageCreator() {
        //     public Message createMessage(Session session) throws JMSException {
        //         //发送商品id
        //         return session.createTextMessage(itemId + "");
        //     }
        // });

        return TaotaoResult.ok();
    }

    public TbItemDesc getItemDescById(long itemId) {
        //查询数据库之前先查询缓存
        try {
            String json = jedisClient.get(ITEM_INFO + ":" + itemId + ":DESC");
            if (!StringUtils.isEmpty(json)) {
                // 把json数据转换成pojo
                return JsonUtils.jsonToPojo(json, TbItemDesc.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //缓存中没有查询数据库
        TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
        try {
            //把查询结果添加到缓存
            jedisClient.set(ITEM_INFO + ":" + itemId + ":DESC", JsonUtils.objectToJson(itemDesc));
            //设置过期时间，提高缓存的利用率
            jedisClient.expire(ITEM_INFO + ":" + itemId + ":DESC", TIEM_EXPIRE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemDesc;
    }
}
