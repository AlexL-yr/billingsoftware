package in.alexlu.billingsoftware.service;

import in.alexlu.billingsoftware.io.CategoryRequest;
import in.alexlu.billingsoftware.io.CategoryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {
    CategoryResponse add(CategoryRequest request, MultipartFile file) throws Exception;

    List<CategoryResponse> read();

    void delete(String categoryId);
}
