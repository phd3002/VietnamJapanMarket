package com.ecommerce.g58.controller.Admin;

import com.ecommerce.g58.entity.ShippingUnit;
import com.ecommerce.g58.service.ShippingUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/add-shipping-unit")
    public String showAddShippingUnitForm(Model model) {
        model.addAttribute("shippingUnit", new ShippingUnit());
        return "admin/add-shipping";
    }

    @PostMapping("/add-shipping-unit")
    public String addShippingUnit(ShippingUnit shippingUnit) {
        shippingUnitService.addShippingUnit(shippingUnit);
        return "redirect:/list-shipping-unit";
    }

    @PostMapping("/delete-shipping-unit/{unitId}")
    public String deleteShippingUnit(@PathVariable("unitId") int id) {
        if (shippingUnitService.existsById(id)) {
            shippingUnitService.deleteShippingUnit(id);
        }
        return "redirect:/list-shipping-unit";
    }
}
