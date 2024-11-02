package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.entity.Countries;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.exception.SpringBootFileUploadException;
import com.ecommerce.g58.service.CountryService;
import com.ecommerce.g58.service.FileS3Service;
import com.ecommerce.g58.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class StoreController {
    @Autowired
    private StoreService storeService;

    @Autowired
    private FileS3Service fileS3Service;

    @Autowired
    private CountryService countryService;

    @GetMapping("/store-info/{storeId}")
    public String getStoreInfo(@PathVariable("storeId") Integer storeId, Model model) {
        Optional<Stores> store = storeService.findById(storeId);
        List<Countries> countries = countryService.getAllCountries();
        if (store.isPresent()) {
            model.addAttribute("store", store.get());
            model.addAttribute("countries", countries);
        } else {
            model.addAttribute("error", "Store information not found.");
        }
        return "seller/store-info-manager";
    }


    @PostMapping("/store-save")
    public String saveStoreInfo(@RequestParam Integer storeId, @RequestParam String storeName,
                                @RequestParam String storePhone, @RequestParam String storeAddress,
                                @RequestParam Integer countryId, @RequestParam String storeDescription,
                                @RequestParam String storeCity, @RequestParam String storeDistrict,
                                @RequestParam String postalCode, @RequestParam String storeMail,
                                @RequestParam MultipartFile storeImg,
                                Model model) throws SpringBootFileUploadException, IOException {
        // Kiểm tra thông tin nhập vào
        if (storeName == null || storeName.isEmpty() || storeName.length() > 100) {
            model.addAttribute("error", "Tên cửa hàng không được để trống và không được vượt quá 100 ký tự.");
            return "redirect:/store-info/" + storeId;
        }
        if (storePhone == null || storePhone.isEmpty() || storePhone.length() > 10) {
            model.addAttribute("error", "Số điện thoại không được để trống và không được vượt quá 10 ký tự.");
            return "redirect:/store-info/" + storeId;
        }
        if (storeAddress == null || storeAddress.isEmpty() || storeAddress.length() > 255) {
            model.addAttribute("error", "Địa chỉ không được để trống và không được vượt quá 255 ký tự.");
            return "redirect:/store-info/" + storeId;
        }
        if (countryId == null) {
            model.addAttribute("error", "Quốc gia không được để trống.");
            return "redirect:/store-info/" + storeId;
        }
        if (storeMail == null || storeMail.isEmpty() || storeMail.length() > 100) {
            model.addAttribute("error", "Email cửa hàng không được để trống và không được vượt quá 100 ký tự.");
            return "redirect:/store-info/" + storeId;
        }
        if (storeDescription != null && storeDescription.length() > 500) {
            model.addAttribute("error", "Mô tả cửa hàng không được vượt quá 500 ký tự.");
            return "redirect:/store-info/" + storeId;
        }
        if (postalCode != null && postalCode.length() > 20) {
            model.addAttribute("error", "Mã bưu chính không được vượt quá 20 ký tự.");
            return "redirect:/store-info/" + storeId;
        }

        // Xác thực quốc gia và cửa hàng
        Optional<Stores> optionalStore = storeService.findById(storeId);
        Optional<Countries> optionalCountry = countryService.findById(countryId);
        if (optionalStore.isPresent() && optionalCountry.isPresent()) {
            Stores store = optionalStore.get();
            store.setStoreName(storeName);
            store.setStorePhone(storePhone);
            store.setStoreAddress(storeAddress);
            store.setCountry(optionalCountry.get());
            store.setStoreDescription(storeDescription);
            store.setStoreMail(storeMail);
            store.setCity(storeCity);
            store.setDistrict(storeDistrict);
            store.setPostalCode(postalCode);
            if (storeImg != null && !storeImg.isEmpty()) {
                String storeImgUrl = fileS3Service.uploadFile(storeImg);
                store.setPictureUrl(storeImgUrl);
            } else {
                store.setPictureUrl(store.getPictureUrl());
            }
            storeService.saveStore(store);
            model.addAttribute("store", store);
            model.addAttribute("message", "Thông tin cửa hàng đã được lưu thành công.");
        } else {
            model.addAttribute("error", "Không tìm thấy thông tin cửa hàng hoặc quốc gia.");
        }
        return "redirect:/store-info/" + storeId;
    }

}
