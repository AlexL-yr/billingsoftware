package in.alexlu.billingsoftware.service.impl;

import ch.qos.logback.core.util.StringUtil;
import in.alexlu.billingsoftware.entity.CategoryEntity;
import in.alexlu.billingsoftware.io.CategoryRequest;
import in.alexlu.billingsoftware.io.CategoryResponse;
import in.alexlu.billingsoftware.repository.CategoryRepository;
import in.alexlu.billingsoftware.repository.ItemRepository;
import in.alexlu.billingsoftware.service.CategoryService;
import in.alexlu.billingsoftware.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final FileUploadService fileUploadService;
    private final ItemRepository itemRepository;
    @Override
    public CategoryResponse add(CategoryRequest request, MultipartFile file) throws IOException{
        String imgUrl =fileUploadService.uploadFile(file);
        //        Uploads images into the local directory function
//        String filename= UUID.randomUUID().toString()+"."+ StringUtils.getFilenameExtension(file.getOriginalFilename());
//        Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
//        Files.createDirectories(uploadPath);
//        Path targetLocation = uploadPath.resolve(filename);
//        Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);
//        String imgUrl = "http://localhost:8080/api/v1.0/uploads/"+filename;
        CategoryEntity newCategory= convertToEntity(request);
        newCategory.setImgUrl(imgUrl);
        newCategory = categoryRepository.save(newCategory);
        return covertToResponse(newCategory);
    }

    @Override
    public List<CategoryResponse> read() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryEntity -> covertToResponse(categoryEntity))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String categoryId) {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                           .orElseThrow(()-> new RuntimeException("Category not found" + categoryId));
        fileUploadService.deleteFile(existingCategory.getImgUrl());
//        Uploads images into the local directory function
//        String imgUrl = existingCategory.getImgUrl();
//        String fileName = imgUrl.substring(imgUrl.lastIndexOf("/")+1);
//        Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
//        Path filePath = uploadPath.resolve(fileName);
//        try{
//            Files.deleteIfExists(filePath);
//        }catch (IOException e){
//            e.printStackTrace();
//        }
        categoryRepository.delete(existingCategory);
    }

    private CategoryResponse covertToResponse(CategoryEntity newCategory) {
        Integer itemsCount = itemRepository.countByCategoryId(newCategory.getId());
        return CategoryResponse.builder()
                .categoryId(newCategory.getCategoryId())
                .name(newCategory.getName())
                .description(newCategory.getDescription())
                .bgColor(newCategory.getBgColor())
                .imgUrl(newCategory.getImgUrl())
                .createdAt(newCategory.getCreatedAt())
                .updatedAt(newCategory.getUpdatedAt())
                .items(itemsCount)
                .build();
    }

    private CategoryEntity convertToEntity(CategoryRequest request) {
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .bgColor(request.getBgColor())
                .build();
    }
}
