package com.cqut.mall.service.impl;

import com.cqut.mall.common.Constant;
import com.cqut.mall.exception.ImoocMallException;
import com.cqut.mall.exception.ImoocMallExceptionEnum;
import com.cqut.mall.filter.UserFilter;
import com.cqut.mall.model.dao.CartMapper;
import com.cqut.mall.model.dao.OrderItemMapper;
import com.cqut.mall.model.dao.OrderMapper;
import com.cqut.mall.model.dao.ProductMapper;
import com.cqut.mall.model.pojo.Order;
import com.cqut.mall.model.pojo.OrderItem;
import com.cqut.mall.model.pojo.Product;
import com.cqut.mall.model.request.CreateOrderReq;
import com.cqut.mall.model.vo.CartVO;
import com.cqut.mall.model.vo.OrderVO;
import com.cqut.mall.service.CartService;
import com.cqut.mall.service.OrderService;
import com.cqut.mall.service.UserService;
import com.cqut.mall.util.OrderCodeFactory;
import com.cqut.mall.util.QRCodeGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private UserService userService;

    @Value("${file.upload.ip}")
    private String ip;

    @Transactional(rollbackFor = Exception.class)/*遇到异常就回滚*/
    @Override
    public String createOrder(CreateOrderReq createOrderReq) {
        // 拿到用户ID
        Integer userId = UserFilter.currentUser.getId();
        // 从购物车中，查找已经勾选的商品
        List<CartVO> cartVOS = cartService.listForCart(userId);
        List<CartVO> temp = new ArrayList<>();
        for (CartVO cartVO : cartVOS) {
            if (cartVO.getSelected().equals(Constant.Cart.SELECTED))
                temp.add(cartVO);
        }
        cartVOS = temp;
        // 如果勾选的商品为空，报错
        if (CollectionUtils.isEmpty(cartVOS))
            throw new ImoocMallException(ImoocMallExceptionEnum.CART_EMPTY);
        // 判断商品是否存在、库存、上下架状态
        this.validSellStatusAndStock(cartVOS);
        // 把购物车对象，转换为item对象
        List<OrderItem> orderItems = this.cartVoListToOrderItemList(cartVOS);
        //  扣库存
        for (OrderItem orderItem : orderItems) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            // 假设订单成功后,计算剩余库存。
            Integer stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0)
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }
        // 把购物车中，已经勾选的商品删除掉
        this.cleanSelectedProduct(cartVOS);

        // 新建订单
        Order order = new Order();
        String orderCode = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderCode);
        order.setUserId(userId);
        order.setTotalPrice(this.countTotalPrice(orderItems));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        // 插入到order表
        orderMapper.insertSelective(order);
        // 插入到order_item
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }
        return order.getOrderNo();
    }

    private Integer countTotalPrice(List<OrderItem> orderItems) {
        Integer totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    private void cleanSelectedProduct(List<CartVO> cartVOS) {
        for (CartVO cartVO : cartVOS) {
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    private List<OrderItem> cartVoListToOrderItemList(List<CartVO> cartVOS) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartVO cartVO : cartVOS) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            //记录商品快照信息
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);

        }
        return orderItemList;
    }

    private void validSellStatusAndStock(List<CartVO> cartVOS) {
        for (CartVO cartVO : cartVOS) {
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            //判断商品是否存在，商品是否上架
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SALE);
            }
            //判断商品库存
            if (cartVO.getQuantity() > product.getStock()) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    @Override
    public OrderVO detailOfOrder(String orderNo) {
        /*使用mybatis的嵌套查询封装好了 orderItemVo*/
        OrderVO orderVO = orderMapper.selectByOrderNo(orderNo);
        //  订单不存在
        if (orderVO == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        //  订单存在，判断所属
        if (!orderVO.getUserId().equals(UserFilter.currentUser.getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
        }
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

    @Override
    public PageInfo listForAllOrder(Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<OrderVO> orderVOS = orderMapper.selectAllOrder(UserFilter.currentUser.getId());
        for (OrderVO orderVO : orderVOS) {
            orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        }

        PageInfo<OrderVO> orderVOPageInfo = new PageInfo<>(orderVOS);
        return orderVOPageInfo;
    }

    @Override
    public void canselOrder(String orderNo) {
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        //查不到订单，报错
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        //验证用户身份
        //订单存在，需要判断所属
        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.CANCEL_WRONG_ORDER_STATUS);
        }
        //恢复商品库存
        //获取订单对应的orderItemList
        List<OrderItem> orderItems = orderItemMapper.selectOrderItemByOrderNo(orderNo);
        for (OrderItem orderItem : orderItems) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() + orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    @Override
    public String qrcode(String orderNo) {
        this.validOrderStatus(orderMapper.selectOrderByOrderNo(orderNo));
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String address = ip + ":" + request.getLocalPort();
        String payUrl = "http://" + address + "/pay?orderNo=" + orderNo;
        try {
            QRCodeGenerator
                    .generateQRCodeImage(payUrl, 350, 350,
                            Constant.FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pngAddress = "http://" + address + "/images/" + orderNo + ".png";
        return pngAddress;
    }

    private void validOrderStatus(Order order) {
        if (!order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode()))
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
    }

    @Override
    public void pay(String orderNo) {
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        //查不到订单，报错
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        this.validOrderStatus(order);
        order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
        order.setPayTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OrderVO> orderVOList = orderMapper.selectAllOrderForAdmin();
        PageInfo<OrderVO> orderVOPageInfo = new PageInfo<>(orderVOList);
        return orderVOPageInfo;
    }

    //发货
    @Override
    public void deliver(String orderNo) {
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        //查不到订单，报错
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus() == Constant.OrderStatusEnum.PAID.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void finish(String orderNo) {
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        //查不到订单，报错
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        //如果是普通用户，就要校验订单的所属
        if (!userService.checkAdminRole(UserFilter.currentUser) && !order.getUserId().equals(UserFilter.currentUser.getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
        }
        //发货后可以完结订单
        if (order.getOrderStatus() == Constant.OrderStatusEnum.DELIVERED.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }
}
