package com.supertiles.ecommerce.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.RazorpayException;
import com.supertiles.ecommerce.dto.Customer;
import com.supertiles.ecommerce.dto.Product;
import com.supertiles.ecommerce.dto.Seller;
import com.supertiles.ecommerce.service.MyService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

	@Autowired
	MyService service;

	@GetMapping("/")
	public String loadHome() {
		return "home.html";
	}

	@GetMapping("/customer-signup")
	public String customerSignup() {
		return "customer-register.html";
	}

	@PostMapping("/customer-signup")
	public String signup(@ModelAttribute Customer customer, HttpSession session) {
		return service.signup(customer, session);
	}

	@GetMapping("/customer-otp/{id}")
	public String loadCustomerOtp(ModelMap map, @PathVariable int id) {
		map.put("id", id);
		return "customer-otp.html";
	}

	@PostMapping("/customer-otp/{id}")
	public String otp(@PathVariable int id, HttpSession session, @RequestParam int otp) {
		return service.otp(id, session, otp);
	}

	@GetMapping("/customer-login")
	public String login() {
		return "customer-login.html";
	}

	@PostMapping("/customer-login")
	public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
		return service.login(email, password, session);
	}

	@GetMapping("/customer-home")
	public String loadCustomerHome(HttpSession session, ModelMap map) {
		return service.loadCustomerHome(session, map);
	}

	@GetMapping("/seller-signup")
	public String sellerSignup() {
		return "seller-register.html";
	}

	@PostMapping("/seller-signup")
	public String signup(@ModelAttribute Seller seller, HttpSession session) {
		return service.signup(seller, session);
	}

	@GetMapping("/seller-otp/{id}")
	public String loadSellerOtp(ModelMap map, @PathVariable int id) {
		map.put("id", id);
		return "seller-otp.html";
	}

	@PostMapping("/seller-otp/{id}")
	public String sellerOtp(@PathVariable int id, HttpSession session, @RequestParam int otp) {
		return service.sellerOtp(id, session, otp);
	}

	@GetMapping("/seller-login")
	public String sellerLogin() {
		return "seller-login.html";
	}

	@PostMapping("/seller-login")
	public String sellerLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
		return service.sellerLogin(email, password, session);
	}

	@GetMapping("/seller-home")
	public String loadSellerHome(HttpSession session, ModelMap map) {
		return service.loadSellerHome(session, map);
	}

	@GetMapping("/add-product")
	public String addProduct(HttpSession session) {
		return service.addProduct(session);
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		return service.logout(session);
	}

	@PostMapping("/add-product")
	public String addProduct(@ModelAttribute Product product, @RequestParam MultipartFile image, HttpSession session)
			throws IOException {
		return service.addProduct(product, image, session);
	}

	@GetMapping("/manage-products")
	public String manageProducts(HttpSession session, ModelMap map) {
		return service.manageProduct(session, map);
	}

	@GetMapping("/delete/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		return service.deleteProduct(id, session);
	}

	@GetMapping("/edit/{id}")
	public String editProduct(@PathVariable int id, HttpSession session, ModelMap map) {
		return service.editProduct(id, session, map);
	}

	@PostMapping("/edit-product")
	public String editProduct(@ModelAttribute Product product, @RequestParam MultipartFile image, HttpSession session)
			throws IOException {
		return service.editProduct(product, image, session);
	}

	@GetMapping("/view-products")
	public String viewProducts(HttpSession session, ModelMap map) {
		return service.viewProducts(session, map);
	}

	@GetMapping("/add-cart/{id}")
	public String addToCart(@PathVariable int id, HttpSession session) {
		return service.addToCart(id, session);
	}

	@GetMapping("/view-cart")
	public String viewCart(HttpSession session, ModelMap map) {
		return service.viewCart(session, map);
	}

	@GetMapping("/increase/{id}")
	public String increase(@PathVariable int id, HttpSession session) {
		return service.increase(id, session);
	}

	@GetMapping("/decrease/{id}")
	public String decrease(@PathVariable int id, HttpSession session) {
		return service.decrease(id, session);
	}
	
	@GetMapping("/proceed")
	public String proceedPayment(HttpSession session,ModelMap map) throws RazorpayException {
		return service.payment(session,map);
	}
	
	@PostMapping("/payment-done/{id}")
	public String paymentDone(HttpSession session,@PathVariable int id,@RequestParam String razorpay_payment_id) {
		return service.paymentDone(session,razorpay_payment_id,id);
	}
}
