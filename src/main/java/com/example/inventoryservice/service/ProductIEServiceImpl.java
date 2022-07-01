package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.IEHistory;
import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.entity.enums.IEHistoryType;
import com.example.inventoryservice.event.OrderDetailDTO;
import com.example.inventoryservice.repository.IEHistoryRepository;
import com.example.inventoryservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProductIEServiceImpl implements ProductIEService {
    private final ProductRepository productRepository;
    private final IEHistoryRepository ieHistoryRepository;

    public ProductIEServiceImpl(ProductRepository productRepository, IEHistoryRepository ieHistoryRepository) {
        this.productRepository = productRepository;
        this.ieHistoryRepository = ieHistoryRepository;
    }

    @Override
    @Transactional
    public boolean exportProductsToOrder(String orderId, Set<OrderDetailDTO> orderDetailDTOSet) {
        List<IEHistory> histories = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        IEHistory history = null;
        for (OrderDetailDTO detail :
                orderDetailDTOSet) {
            Product product = productRepository.findProductById(detail.getProductId());
            if (product != null) {
                if (product.getInStockQty() >= detail.getQuantity()) {
                    int afterExportQty = product.getInStockQty() - detail.getQuantity();
                    product.setInStockQty(afterExportQty);
                    history = IEHistory.builder()
                            .product(product)
                            .currentProductQty(afterExportQty)
                            .orderId(orderId)
                            .quantity(detail.getQuantity())
                            .type(IEHistoryType.EXPORT)
                            .success(true)
                            .build();
                    histories.add(history);
                    products.add(product);
                    if (histories.size() != 0 && products.size() != 0 && histories.size() == products.size()) {
                        productRepository.saveAll(products);
                        ieHistoryRepository.saveAll(histories);
                        return true;
                    }
                } else {
                    history = IEHistory.builder()
                            .product(product)
                            .currentProductQty(product.getInStockQty())
                            .orderId(orderId)
                            .quantity(detail.getQuantity())
                            .type(IEHistoryType.EXPORT)
                            .success(false)
                            .build();
                    histories.add(history);
                    ieHistoryRepository.saveAll(histories);
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional
    public boolean returnProductsFromOrder(String orderId) {
        Set<IEHistory> histories = ieHistoryRepository.findAllByOrderId(orderId);
        List<Product> returnProducts = new ArrayList<>();
        List<IEHistory> returnHistories = new ArrayList<>();
        IEHistory returnHistory;
        if (histories.size() > 0) {
            for (IEHistory history :
                    histories) {
                Product product = history.getProduct();
                if (product != null) {
                    int afterReturnQty = product.getInStockQty() + history.getQuantity();
                    product.setInStockQty(product.getInStockQty() + history.getQuantity());
                    returnHistory = IEHistory.builder()
                            .type(IEHistoryType.RETURN)
                            .quantity(history.getQuantity())
                            .currentProductQty(afterReturnQty)
                            .orderId(history.getOrderId())
                            .product(product)
                            .success(true)
                            .build();
                    returnHistories.add(returnHistory);
                    returnProducts.add(product);
                }
            }
        }
        if (returnHistories.size() != 0 && returnHistories.size() == histories.size() && returnProducts.size() == returnHistories.size()) {
            productRepository.saveAll(returnProducts);
            ieHistoryRepository.saveAll(returnHistories);
            return true;
        }
        return false;
    }

}
