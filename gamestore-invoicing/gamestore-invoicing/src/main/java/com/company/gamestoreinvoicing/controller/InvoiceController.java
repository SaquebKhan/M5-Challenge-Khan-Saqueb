package com.company.gamestoreinvoicing.controller;


import com.company.gamestoreinvoicing.model.Invoice;
import com.company.gamestoreinvoicing.service.GameStoreServiceLayer;
import com.company.gamestoreinvoicing.viewModel.InvoiceViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/invoice")
@CrossOrigin(origins = {"http://localhost:3000"})
public class InvoiceController {

    @Autowired
    GameStoreServiceLayer service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceViewModel purchaseItem(@RequestBody @Valid InvoiceViewModel invoiceViewModel) {
        invoiceViewModel = service.createInvoice(invoiceViewModel);
        return invoiceViewModel;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Invoice findInvoice(@PathVariable("id") long invoiceId) {
        Invoice invoice = service.getInvoice(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice could not be retrieved for id " + invoiceId);
        } else {
            return invoice;
        }
    }
    @DeleteMapping ("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvoice(@PathVariable("id")long invoiceId) {
        service.deleteInvoice(invoiceId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Invoice> findAllInvoices() {
        List<Invoice> InvoiceList = service.getAllInvoices();

        if (InvoiceList == null || InvoiceList.isEmpty()) {
            throw new IllegalArgumentException("No invoices were found.");
        } else {
            return InvoiceList;
        }
    }

    @GetMapping("/cname/{name}")
    @ResponseStatus(HttpStatus.OK)
    public List<InvoiceViewModel> findInvoicesByCustomerName(@PathVariable String name) {
        List<InvoiceViewModel> invoiceViewModelList = service.getInvoicesByCustomerName(name);

        if (invoiceViewModelList == null || invoiceViewModelList.isEmpty()) {
            throw new IllegalArgumentException("No invoices were found for: "+name);
        } else {
            return invoiceViewModelList;
        }
    }
}
