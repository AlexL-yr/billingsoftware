package in.alexlu.billingsoftware.service;

import in.alexlu.billingsoftware.io.ItemRequest;
import in.alexlu.billingsoftware.io.ItemResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {

    ItemResponse add(ItemRequest request, MultipartFile file);
    List<ItemResponse> fetchItems();
    void delete(String itemId);
}
