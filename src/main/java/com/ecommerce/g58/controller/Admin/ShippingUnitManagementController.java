package com.ecommerce.g58.controller.Admin;

import com.ecommerce.g58.entity.ShippingUnit;
import com.ecommerce.g58.service.ShippingUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ShippingUnitManagementController {
    @Autowired
    private ShippingUnitService shippingUnitService;

    @GetMapping("/list-shipping-unit")
    public String listShippingUnits(Model model) {
        List<ShippingUnit> shippingUnits = shippingUnitService.getAllShippingUnits();
        model.addAttribute("shippingUnits", shippingUnits);
        return "admin/shipping-manager";
    }

    @PostMapping("/add-shipping-unit")
    public String addShippingUnit(ShippingUnit shippingUnit) {
        shippingUnitService.addShippingUnit(shippingUnit);
        return "redirect:/list-shipping-unit";
    }

    @PostMapping("/delete-shipping-unit")
    public String deleteShippingUnit(Integer id) {
        if (shippingUnitService.existsById(id)) {
            shippingUnitService.deleteShippingUnit(id);
        }
        return "redirect:/list-shipping-unit";
    }
}
