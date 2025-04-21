package com.supertiles.ecommerce.service;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.supertiles.ecommerce.dto.Cart;
import com.supertiles.ecommerce.dto.Customer;
import com.supertiles.ecommerce.dto.Eorder;
import com.supertiles.ecommerce.dto.Item;
import com.supertiles.ecommerce.dto.Product;
import com.supertiles.ecommerce.dto.Seller;
import com.supertiles.ecommerce.repository.CustomerRepository;
import com.supertiles.ecommerce.repository.EorderRepository;
import com.supertiles.ecommerce.repository.ItemRepository;
import com.supertiles.ecommerce.repository.ProductRepository;
import com.supertiles.ecommerce.repository.SellerRepository;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Service
public class MyService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	EorderRepository eorderRepository;

	@Autowired
	SellerRepository sellerRepository;

	@Autowired
	JavaMailSender mailSender;

	@Value("${razorpay.key}")
	String key;

	@Value("${razorpay.secret}")
	String secret;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	ItemRepository itemRepository;

	public String signup(Customer customer, HttpSession session) {
		if (customerRepository.existsByEmail(customer.getEmail())) {
			session.setAttribute("fail", "Email Already Exists");
			return "redirect:/customer-signup";
		} else if (customerRepository.existsByMobile(customer.getMobile())) {
			session.setAttribute("fail", "Mobile Number Already Exists");
			return "redirect:/customer-signup";
		} else {
			int otp = new Random().nextInt(100000, 1000000);
			customer.setOtp(otp);
			customer.setPassword(AES.encrypt(customer.getPassword(), "123"));
			customerRepository.save(customer);
			// sendEmail(customer.getEmail(), customer.getName(), otp);
			session.setAttribute("pass", "Otp Sent Success");
			return "redirect:/customer-otp/" + customer.getId();
		}
	}

	public String otp(int id, HttpSession session, int otp) {
		Customer customer = customerRepository.findById(id).orElseThrow();
		if (customer.getOtp() == otp) {
			customer.setVerified(true);
			customerRepository.save(customer);
			session.setAttribute("pass", "Account Created Success");
			return "redirect:/";
		} else {
			session.setAttribute("fail", "Invalid OTP");
			return "redirect:/customer-otp/" + customer.getId();
		}
	}

	public String login(String email, String password, HttpSession session) {
		Customer customer = customerRepository.findByEmail(email);
		if (customer == null) {
			session.setAttribute("fail", "Incorrect Email");
			return "redirect:/customer-login";
		} else {
			if (password.equals(AES.decrypt(customer.getPassword(), "123"))) {
				if (customer.isVerified()) {
					session.setAttribute("customer", customer);
					session.setAttribute("pass", "Login Success");
					return "redirect:/customer-home";
				} else {
					int otp = new Random().nextInt(100000, 1000000);
					customer.setOtp(otp);
					customerRepository.save(customer);
					// sendEmail(customer.getEmail(), customer.getName(), otp);
					session.setAttribute("pass", "Otp Sent Success");
					return "redirect:/customer-otp/" + customer.getId();
				}
			} else {
				session.setAttribute("fail", "Incorrect Password");
				return "redirect:/customer-login";
			}
		}
	}

	public String loadCustomerHome(HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
			return "customer-home.html";
		} else {
			map.put("fail", "Invalid Session");
			return "customer-login.html";
		}
	}

	public String signup(Seller seller, HttpSession session) {
		if (sellerRepository.existsByEmail(seller.getEmail())) {
			session.setAttribute("fail", "Email Already Exists");
			return "redirect:/seller-register";
		} else if (sellerRepository.existsByMobile(seller.getMobile())) {
			session.setAttribute("fail", "Mobile Number Already Exists");
			return "redirect:/seller-register";
		} else {
			int otp = new Random().nextInt(100000, 1000000);
			seller.setOtp(otp);
			seller.setPassword(AES.encrypt(seller.getPassword(), "123"));
			sellerRepository.save(seller);
			// sendEmail(seller.getEmail(), seller.getName(), otp);
			session.setAttribute("pass", "Otp Sent Success");
			return "redirect:/seller-otp/" + seller.getId();
		}
	}

	public String sellerOtp(int id, HttpSession session, int otp) {
		Seller seller = sellerRepository.findById(id).orElseThrow();
		if (seller.getOtp() == otp) {
			seller.setVerified(true);
			sellerRepository.save(seller);
			session.setAttribute("pass", "Account Created Success");
			return "redirect:/";
		} else {
			session.setAttribute("fail", "Invalid OTP");
			return "redirect:/seller-otp/" + seller.getId();
		}
	}

	public String sellerLogin(String email, String password, HttpSession session) {
		Seller seller = sellerRepository.findByEmail(email);
		if (seller == null) {
			session.setAttribute("fail", "Incorrect Email");
			return "redirect:/seller-login";
		} else {
			if (password.equals(AES.decrypt(seller.getPassword(), "123"))) {
				if (seller.isVerified()) {
					session.setAttribute("pass", "Login Success");
					session.setAttribute("seller", seller);
					return "redirect:/seller-home";
				} else {
					int otp = new Random().nextInt(100000, 1000000);
					seller.setOtp(otp);
					sellerRepository.save(seller);
					// sendEmail(seller.getEmail(), seller.getName(), otp);
					session.setAttribute("pass", "Otp Sent Success");
					return "redirect:/seller-otp/" + seller.getId();
				}
			} else {
				session.setAttribute("fail", "Incorrect Password");
				return "seller-login.html";
			}
		}
	}

	public String loadSellerHome(HttpSession session, ModelMap map) {
		if (session.getAttribute("seller") != null) {
			return "seller-home.html";
		} else {
			map.put("fail", "Invalid Session");
			return "seller-login.html";
		}
	}

	public void sendEmail(String email, String name, int otp) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setTo(email);
			helper.setSubject("Email Verification with SuperSprings");
			helper.setFrom("sathiyaseelaperumal7@gmail.com", "springs");
			helper.setText("<h1>Hello " + name + " Your OTP is : " + otp + "</h1>", true);
		} catch (Exception e) {
		}
		mailSender.send(message);
	}

	public void removeMessage() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes();
		HttpSession session = attributes.getRequest().getSession(true);
		session.removeAttribute("pass");
		session.removeAttribute("fail");
	}

	public String addProduct(HttpSession session) {
		if (session.getAttribute("seller") != null) {
			return "add-product.html";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/seller-login";
		}
	}

	public String logout(HttpSession session) {
		session.removeAttribute("customer");
		session.removeAttribute("seller");
		session.setAttribute("pass", "Logout Success");
		return "redirect:/";
	}

	public String addProduct(Product product, MultipartFile image, HttpSession session) throws IOException {
		if (session.getAttribute("seller") != null) {

			byte[] picture = new byte[image.getInputStream().available()];
			image.getInputStream().read(picture);

			product.setPicture(picture);

			Seller seller = (Seller) session.getAttribute("seller");

			product.setSeller(seller);

			productRepository.save(product);

			session.setAttribute("pass", "Product Added Success");
			return "redirect:/seller-home";

		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/seller-login";
		}
	}

	public String manageProduct(HttpSession session, ModelMap map) {
		if (session.getAttribute("seller") != null) {
			Seller seller = (Seller) session.getAttribute("seller");
			List<Product> list = productRepository.findBySeller(seller);
			if (list.isEmpty()) {
				session.setAttribute("fail", "No Products Added Yet");
				return "redirect:/seller-home";
			} else {
				map.put("list", list);
				return "products.html";
			}
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/seller-login";
		}
	}

	public String deleteProduct(int id, HttpSession session) {
		if (session.getAttribute("seller") != null) {
			productRepository.deleteById(id);
			session.setAttribute("pass", "PRoduct Deleted Success");
			return "redirect:/manage-products";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/seller-login";
		}
	}

	public String editProduct(int id, HttpSession session, ModelMap map) {
		if (session.getAttribute("seller") != null) {
			Product product = productRepository.findById(id).orElse(null);
			map.put("product", product);
			return "edit-product.html";

		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/seller-login";
		}
	}

	public String editProduct(Product product, MultipartFile image, HttpSession session) throws IOException {
		if (session.getAttribute("seller") != null) {

			byte[] picture = new byte[image.getInputStream().available()];
			image.getInputStream().read(picture);

			if (picture.length != 0)
				product.setPicture(picture);
			else
				product.setPicture(productRepository.findById(product.getId()).orElse(null).getPicture());

			Seller seller = (Seller) session.getAttribute("seller");

			product.setSeller(seller);

			productRepository.save(product);

			session.setAttribute("pass", "Product Updated Success");
			return "redirect:/manage-products";

		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/seller-login";
		}
	}

	public String viewProducts(HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
			List<Product> list = productRepository.findAll();
			if (list.isEmpty()) {
				session.setAttribute("fail", "No Products Added Yet");
				return "redirect:/customer-home";
			} else {
				map.put("list", list);
				return "view-products.html";
			}
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "customer-login.html";
		}
	}

	public String addToCart(int id, HttpSession session) {
		if (session.getAttribute("customer") != null) {

			Product product = productRepository.findById(id).orElseThrow();
			Customer customer = (Customer) session.getAttribute("customer");
			if (product.getStock() > 0) {
				Cart cart = customer.getCart();
				List<Item> items = cart.getItems();

				boolean flag = true;
				for (Item item : items) {
					if (item.getName().equals(product.getName()) && item.getBrand().equals(product.getBrand())) {
						flag = false;
						break;
					}
				}
				if (flag) {
					Item item = new Item();
					item.setBrand(product.getBrand());
					item.setDescription(product.getDescription());
					item.setName(product.getName());
					item.setPicture(product.getPicture());
					item.setPrice(product.getPrice());
					item.setQuantity(1);
					item.setSize(product.getSize());
					items.add(item);
					customerRepository.save(customer);
					session.setAttribute("customer", customerRepository.findById(customer.getId()).orElseThrow());

					product.setStock(product.getStock() - 1);
					productRepository.save(product);

					session.setAttribute("pass", "Product Added Success");
					return "redirect:/view-products";
				} else {
					session.setAttribute("fail", "Product Already in Cart");
					return "redirect:/view-cart";
				}

			} else {
				session.setAttribute("fail", "Out of Stock");
				return "redirect:/customer-home";
			}

		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/customer-login";
		}
	}

	public String viewCart(HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
			Customer customer = (Customer) session.getAttribute("customer");
			List<Item> items = customer.getCart().getItems();
			if (items.isEmpty()) {
				session.setAttribute("fail", "No Items in Cart");
				return "redirect:/customer-home";
			} else {
				double sum = items.stream().mapToDouble(i -> (i.getPrice() * i.getQuantity())).sum();
				map.put("tp", sum);
				map.put("items", items);
				return "cart.html";
			}
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/customer-login";
		}
	}

	public String increase(int id, HttpSession session) {
		if (session.getAttribute("customer") != null) {
			Customer customer = (Customer) session.getAttribute("customer");
			Item item = itemRepository.findById(id).orElseThrow();

			Product product = productRepository.findByNameAndBrandAndPrice(item.getName(), item.getBrand(),
					item.getPrice());

			if (product.getStock() == 0) {
				session.setAttribute("fail", "Out of Stock");
				return "redirect:/view-cart";
			} else {
				item.setQuantity(item.getQuantity() + 1);
				itemRepository.save(item);
				product.setStock(product.getStock() - 1);
				productRepository.save(product);
				session.setAttribute("pass", "Incremented Success");
				session.setAttribute("customer", customerRepository.findById(customer.getId()).orElseThrow());
				return "redirect:/view-cart";
			}

		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/customer-login";
		}
	}

	public String decrease(int id, HttpSession session) {
		if (session.getAttribute("customer") != null) {
			Customer customer = (Customer) session.getAttribute("customer");
			Item item = itemRepository.findById(id).orElseThrow();

			Product product = productRepository.findByNameAndBrandAndPrice(item.getName(), item.getBrand(),
					item.getPrice());

			if (item.getQuantity() > 1) {
				item.setQuantity(item.getQuantity() - 1);
				itemRepository.save(item);

				product.setStock(product.getStock() + 1);
				productRepository.save(product);

				session.setAttribute("pass", "Decremented Success");
				session.setAttribute("customer", customerRepository.findById(customer.getId()).orElseThrow());
				return "redirect:/view-cart";
			} else {

				customer.getCart().getItems().remove(item);
				customerRepository.save(customer);

				itemRepository.deleteById(id);
				session.setAttribute("pass", "Decremented Success");
				session.setAttribute("customer", customerRepository.findById(customer.getId()).orElseThrow());
				return "redirect:/view-cart";
			}

		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/customer-login";
		}
	}

	public String payment(HttpSession session, ModelMap map) throws RazorpayException {
		if (session.getAttribute("customer") != null) {
			Customer customer = (Customer) session.getAttribute("customer");
			List<Item> items = customer.getCart().getItems();

			double amount = items.stream().mapToDouble(i -> (i.getPrice() * i.getQuantity())).sum();

			RazorpayClient razorpay = new RazorpayClient(key, secret);

			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", amount * 100);
			orderRequest.put("currency", "INR");

			Order order = razorpay.orders.create(orderRequest);
			String orderId = order.get("id");

			Eorder eorder = new Eorder();
			eorder.setItems(items);
			eorder.setOrderId(orderId);
			eorder.setPrice(amount);

			customer.getEorders().add(eorder);

			customerRepository.save(customer);

			session.setAttribute("customer", customerRepository.findById(customer.getId()).orElseThrow());

			map.put("key", key);
			map.put("eorder", eorder);
			map.put("currency", "INR");
			map.put("customer", customer);
			map.put("url", "/payment-done/" + eorderRepository.findByOrderId(orderId).getId());
			return "payment.html";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/customer-login";
		}
	}

	public String paymentDone(HttpSession session, String razorpay_payment_id, int id) {
		if (session.getAttribute("customer") != null) {
			Customer customer = (Customer) session.getAttribute("customer");
			Eorder eorder = eorderRepository.findById(id).orElseThrow();
			eorder.setStatus(true);
			eorder.setPaymentId(razorpay_payment_id);
			eorderRepository.save(eorder);
			customer.getCart().getItems().clear();
			customerRepository.save(customer);
			session.setAttribute("pass", "Order Placed");
			session.setAttribute("customer", customerRepository.findById(customer.getId()).orElseThrow());
			return "redirect:/customer-home";

		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/customer-login";
		}
	}

}
