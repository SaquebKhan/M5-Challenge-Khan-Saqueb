package com.company.gamestoreinvoicing.service;

import com.company.gamestoreinvoicing.model.Invoice;
import com.company.gamestoreinvoicing.repository.InvoiceRepository;
import com.company.gamestoreinvoicing.repository.ProcessingFeeRepository;
import com.company.gamestoreinvoicing.repository.TaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.company.gamestoreinvoicing.viewModel.InvoiceViewModel;

@Component
public class GameStoreServiceLayer {

    private final BigDecimal PROCESSING_FEE = new BigDecimal("15.49");
    private final BigDecimal MAX_INVOICE_TOTAL = new BigDecimal("999.99");

    com.company.gamestoreinvoicing.repository.InvoiceRepository invoiceRepo;
    com.company.gamestoreinvoicing.repository.TaxRepository taxRepo;
    com.company.gamestoreinvoicing.repository.ProcessingFeeRepository processingFeeRepo;

    @Autowired
    public GameStoreServiceLayer(InvoiceRepository invoiceRepo, TaxRepository taxRepo, ProcessingFeeRepository processingFeeRepo) {
    this.invoiceRepo = invoiceRepo;
    this.taxRepo = taxRepo;
    this.processingFeeRepo = processingFeeRepo;
    }

    public InvoiceViewModel createInvoice(InvoiceViewModel invoiceViewModel) {

        //validation...
        if (invoiceViewModel==null)
            throw new NullPointerException("Create invoice failed. no invoice data.");

        if(invoiceViewModel.getItemType()==null)
            throw new IllegalArgumentException("Unrecognized Item type. Valid ones: Console or Game");

        //Check Quantity is > 0...
        if(invoiceViewModel.getQuantity()<=0){
            throw new IllegalArgumentException(invoiceViewModel.getQuantity() +
                    ": Unrecognized Quantity. Must be > 0.");
        }

        //start building invoice...
        com.company.gamestoreinvoicing.model.Invoice invoice = new com.company.gamestoreinvoicing.model.Invoice();
        invoice.setName(invoiceViewModel.getName());
        invoice.setStreet(invoiceViewModel.getStreet());
        invoice.setCity(invoiceViewModel.getCity());
        invoice.setState(invoiceViewModel.getState());
        invoice.setZipcode(invoiceViewModel.getZipcode());
        invoice.setItemType(invoiceViewModel.getItemType());
        invoice.setItemId(invoiceViewModel.getItemId());

 

        invoice.setQuantity(invoiceViewModel.getQuantity());

        invoice.setSubtotal(
                invoice.getUnitPrice().multiply(
                        new BigDecimal(invoiceViewModel.getQuantity())).setScale(2, RoundingMode.HALF_UP));

        //Throw Exception if subtotal is greater than 999.99
        if ((invoice.getSubtotal().compareTo(new BigDecimal(999.99)) > 0)) {
            throw new IllegalArgumentException("Subtotal exceeds maximum purchase price of $999.99");
        }

        //Validate State and Calc tax...
        BigDecimal tempTaxRate;
        Optional<com.company.gamestoreinvoicing.model.Tax> returnVal = taxRepo.findById(invoice.getState());

        if (returnVal.isPresent()) {
            tempTaxRate = returnVal.get().getRate();
        } else {
            throw new IllegalArgumentException(invoice.getState() + ": Invalid State code.");
        }

        if (!tempTaxRate.equals(BigDecimal.ZERO))
            invoice.setTax(tempTaxRate.multiply(invoice.getSubtotal()));
        else
            throw new IllegalArgumentException( invoice.getState() + ": Invalid State code.");

        BigDecimal processingFee;
        Optional<com.company.gamestoreinvoicing.model.ProcessingFee> returnVal2 = processingFeeRepo.findById(invoiceViewModel.getItemType());

        if (returnVal2.isPresent()) {
            processingFee = returnVal2.get().getFee();
        } else {
            throw new IllegalArgumentException("Requested item is unavailable.");
        }

        invoice.setProcessingFee(processingFee);

        //Checks if quantity of items if greater than 10 and adds additional processing fee
        if (invoiceViewModel.getQuantity() > 10) {
            invoice.setProcessingFee(invoice.getProcessingFee().add(PROCESSING_FEE));
        }

        invoice.setTotal(invoice.getSubtotal().add(invoice.getProcessingFee()).add(invoice.getTax()));

        //checks total for validation
        if ((invoice.getTotal().compareTo(MAX_INVOICE_TOTAL) > 0)) {
            throw new IllegalArgumentException("Subtotal exceeds maximum purchase price of $999.99");
        }

        invoice = invoiceRepo.save(invoice);

        return buildInvoiceViewModel(invoice);
    }

    public InvoiceViewModel getInvoice(long id) {
        Optional<com.company.gamestoreinvoicing.model.Invoice> invoice = invoiceRepo.findById(id);
        if (invoice == null)
            return null;
        else
            return buildInvoiceViewModel(invoice.get());
    }

    public List<InvoiceViewModel> getAllInvoices() {
        List<com.company.gamestoreinvoicing.model.Invoice> invoiceList = invoiceRepo.findAll();
        List<InvoiceViewModel> ivmList = new ArrayList<>();
        List<InvoiceViewModel> exceptionList = null;

        if (invoiceList == null) {
            return exceptionList;
        } else {
            invoiceList.stream().forEach(i -> {
                ivmList.add(buildInvoiceViewModel(i));
            });
        }
        return ivmList;
    }

    public List<InvoiceViewModel> getInvoicesByCustomerName(String name) {
        List<com.company.gamestoreinvoicing.model.Invoice> invoiceList = invoiceRepo.findByName(name);
        List<InvoiceViewModel> ivmList = new ArrayList<>();
        List<InvoiceViewModel> exceptionList = null;

        if (invoiceList == null) {
            return exceptionList;
        } else {
            invoiceList.stream().forEach(i -> ivmList.add(buildInvoiceViewModel(i)));
        }
        return ivmList;
    }

    private InvoiceViewModel buildInvoiceViewModel(Invoice i) {
        return null;
    }

    public void deleteInvoice(long id){
        invoiceRepo.deleteById(id);
    }
    
}