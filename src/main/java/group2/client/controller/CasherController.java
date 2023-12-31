/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package group2.client.controller;

import group2.client.entities.*;
import group2.client.exception.EmailAlreadyExistsException;
import group2.client.repository.AdminRepository;
import group2.client.repository.CasherRepository;
import group2.client.repository.DoctorRepository;
import group2.client.repository.PatientRepository;
import group2.client.service.AuthService;
import group2.client.service.CasherService;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;

/**
 *
 * @author Ann
 */
@Controller
public class CasherController {

    String apiUrl = "http://localhost:8888/api/casher";
    private String apiUrlTPK = "http://localhost:8888/api/taophieukham/";
    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CasherRepository casherRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    CasherService casherService;

    private boolean emailExistsInAnyRole(String... emails) {
        for (String email : emails) {
            if (email != null
                    && (casherRepository.existsByEmail(email)
                    || adminRepository.existsByEmail(email)
                    || doctorRepository.existsByEmail(email)
                    || patientRepository.existsByEmail(email))) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping("/admin/casher")
    public String page(Model model, HttpServletRequest request) {

        Admin currentAdmin = authService.isAuthenticatedAdmin(request);
        Doctor currentDoctor = authService.isAuthenticatedDoctor(request);
        Patient currentPatient = authService.isAuthenticatedPatient(request);
        Casher currentCasher = authService.isAuthenticatedCasher(request);

        if (currentPatient != null && currentPatient.getRole().equals("PATIENT")) {
            return "redirect:/forbien";
        } else if (currentAdmin != null && currentAdmin.getRole().equals("ADMIN")) {
            ResponseEntity<List<Casher>> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Casher>>() {
            });
            // Kiểm tra mã trạng thái của phản hồi
            if (response.getStatusCode().is2xxSuccessful()) {
                List<Casher> listCasher = response.getBody();
                // Xử lý dữ liệu theo nhu cầu của bạn
                model.addAttribute("listCasher", listCasher);
                model.addAttribute("currentAdmin", currentAdmin);
            }
            return "/admin/casher/index";
        } else if (currentDoctor != null && currentDoctor.getRole().equals("DOCTOR")) {
            ResponseEntity<List<Casher>> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Casher>>() {
            });
            // Kiểm tra mã trạng thái của phản hồi
            if (response.getStatusCode().is2xxSuccessful()) {
                List<Casher> listCasher = response.getBody();
                // Xử lý dữ liệu theo nhu cầu của bạn
                model.addAttribute("listCasher", listCasher);
                model.addAttribute("currentDoctor", currentDoctor);
            }
            return "/admin/casher/index";
        } else if (currentCasher != null && currentCasher.getRole().equals("CASHER")) {
            ResponseEntity<List<Casher>> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Casher>>() {
            });
            // Kiểm tra mã trạng thái của phản hồi
            if (response.getStatusCode().is2xxSuccessful()) {
                List<Casher> listCasher = response.getBody();
                // Xử lý dữ liệu theo nhu cầu của bạn
                model.addAttribute("listCasher", listCasher);
                model.addAttribute("currentCasher", currentCasher);
            }
            return "/admin/casher/index";
        } else {
            return "redirect:/login";
        }

    }

    @RequestMapping("/admin/casher/create")
    public String create(Model model, HttpServletRequest request) {

        Admin currentAdmin = authService.isAuthenticatedAdmin(request);
        Doctor currentDoctor = authService.isAuthenticatedDoctor(request);
        Patient currentPatient = authService.isAuthenticatedPatient(request);
        Casher currentCasher = authService.isAuthenticatedCasher(request);

        if (currentPatient != null && currentPatient.getRole().equals("PATIENT")) {
            return "redirect:/forbien";
        } else if (currentAdmin != null && currentAdmin.getRole().equals("ADMIN")) {
            // Tạo một đối tượng Casher trống để gửi thông tin tới form tạo mới
            model.addAttribute("casher", new Casher());
            model.addAttribute("currentAdmin", currentAdmin);
            return "/admin/casher/create";
        } else if (currentDoctor != null && currentDoctor.getRole().equals("DOCTOR")) {
            // Tạo một đối tượng Casher trống để gửi thông tin tới form tạo mới
            model.addAttribute("casher", new Casher());
            model.addAttribute("currentDoctor", currentDoctor);
            return "/admin/casher/create";
        } else if (currentCasher != null && currentCasher.getRole().equals("CASHER")) {
            // Tạo một đối tượng Casher trống để gửi thông tin tới form tạo mới
            model.addAttribute("casher", new Casher());
            model.addAttribute("currentCasher", currentCasher);
            return "/admin/casher/create";
        } else {
            return "redirect:/login";
        }

    }

    @RequestMapping(value = "/admin/casher/create", method = RequestMethod.POST)
    public String create(Model model, @Valid @ModelAttribute Casher casher, BindingResult bindingResult, HttpServletRequest requestCurrent) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Admin currentAdmin = authService.isAuthenticatedAdmin(requestCurrent);

        if (bindingResult.hasErrors()) {
            model.addAttribute("casher", casher);
            model.addAttribute("currentAdmin", currentAdmin);
            return "/admin/casher/create";
        }

        // Kiểm tra xem email đã tồn tại trong cơ sở dữ liệu chưa
        if (casherRepository.existsByEmail(casher.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Email already exists.");
            model.addAttribute("currentAdmin", currentAdmin);
            model.addAttribute("casher", casher);
            return "/admin/casher/create";
        }

        // Tạo một HttpEntity với thông tin Casher để gửi yêu cầu POST
        HttpEntity<Casher> request = new HttpEntity<>(casher, headers);

        ResponseEntity<Casher> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, Casher.class);
        // Kiểm tra mã trạng thái của phản hồi
        if (response.getStatusCode().is2xxSuccessful()) {
            // Thực hiện thêm xử lý sau khi tạo Casher thành công (nếu cần)

            // Chuyển hướng về trang danh sách Casher
            return "redirect:/admin/casher";
        } else {
            model.addAttribute("casher", casher);
            return "/admin/casher/create";
        }
    }

    @RequestMapping(value = "/admin/casher/details/{id}", method = RequestMethod.GET)
    public String details(Model model, @PathVariable("id") Integer id, HttpServletRequest request) {

        Admin currentAdmin = authService.isAuthenticatedAdmin(request);
        Doctor currentDoctor = authService.isAuthenticatedDoctor(request);
        Patient currentPatient = authService.isAuthenticatedPatient(request);
        Casher currentCasher = authService.isAuthenticatedCasher(request);

        if (currentPatient != null && currentPatient.getRole().equals("PATIENT")) {
            return "redirect:/forbien";
        } else if (currentAdmin != null && currentAdmin.getRole().equals("ADMIN")) {
            Casher casher = restTemplate.getForObject(apiUrl + "/" + id, Casher.class);
            model.addAttribute("casher", casher);
            model.addAttribute("currentAdmin", currentAdmin);
            return "/admin/casher/details";
        } else if (currentDoctor != null && currentDoctor.getRole().equals("DOCTOR")) {
            Casher casher = restTemplate.getForObject(apiUrl + "/" + id, Casher.class);
            model.addAttribute("casher", casher);
            model.addAttribute("currentDoctor", currentDoctor);
            return "/admin/casher/details";
        } else if (currentCasher != null && currentCasher.getRole().equals("CASHER")) {
            Casher casher = restTemplate.getForObject(apiUrl + "/" + id, Casher.class);
            model.addAttribute("casher", casher);
            model.addAttribute("currentCasher", currentCasher);
            return "/admin/casher/details";
        } else {
            return "redirect:/login";
        }

    }

//    @RequestMapping(value = "/admin/casher/edit", method = RequestMethod.POST)
//    public String update(Model model, @Valid @ModelAttribute Casher updatedCasher, BindingResult bindingResult) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("casher", updatedCasher);
//            return "/admin/casher/edit";
//        }
//
//        // Lấy Casher hiện có từ API server
//        Casher existingCasher = restTemplate.getForObject(apiUrl + "/" + updatedCasher.getId(), Casher.class);
//
//        // Bổ sung id vào URL khi thực hiện PUT
//        String url = apiUrl + "/" + updatedCasher.getId();
//
//        // Tạo một HttpEntity với thông tin Casher cập nhật để gửi yêu cầu PUT
//        HttpEntity<Casher> request = new HttpEntity<>(updatedCasher, headers);
//
//        try {
//            restTemplate.exchange(url, HttpMethod.PUT, request, Casher.class);
//            return "redirect:/admin/casher";
//
//        } catch (RestClientException e) {
//            model.addAttribute("casher", existingCasher);
//            return "/admin/casher/edit/" + updatedCasher.getId();
//        }
//    }

//    @RequestMapping(value = "/admin/casher/changepassword/{id}", method = RequestMethod.GET)
//    public String changePass(Model model, @PathVariable("id") Integer id, HttpServletRequest request) {
//
//        Admin currentAdmin = authService.isAuthenticatedAdmin(request);
//        Doctor currentDoctor = authService.isAuthenticatedDoctor(request);
//        Patient currentPatient = authService.isAuthenticatedPatient(request);
//        Casher currentCasher = authService.isAuthenticatedCasher(request);
//
//        if (currentPatient != null && currentPatient.getRole().equals("PATIENT")) {
//            return "redirect:/forbien";
//        } else if (currentAdmin != null && currentAdmin.getRole().equals("ADMIN")) {
//            Casher casher = restTemplate.getForObject(apiUrl + "/" + id, Casher.class);
//            model.addAttribute("casher", casher);
//            model.addAttribute("currentAdmin", currentAdmin);
//            return "/admin/casher/changepassword";
//        } else if (currentDoctor != null && currentDoctor.getRole().equals("DOCTOR")) {
//            return "redirect:/forbien";
//        } else if (currentCasher != null && currentCasher.getRole().equals("CASHER")) {
//            return "redirect:/forbien";
//        } else {
//            return "redirect:/login";
//        }
//
//    }
//
//    @RequestMapping(value = "/admin/casher/changepassword", method = RequestMethod.POST)
//    public String changePass(Model model, @Valid @ModelAttribute Casher changePassword, BindingResult bindingResult, @RequestParam("newPass") String newPass, HttpSession session) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
////        if (bindingResult.hasErrors()) {
////            return "/admin/casher/changepassword";
////        }
//        
//        Casher casher = casherService.getCasherById(changePassword.getId());
//
//        if (newPass == "") {
//            session.setAttribute("error", "The new password cannot be blank!!!");
//            return "/admin/casher/changepassword";
//        } else {
//            casher.setPassword(newPass);
//        }
//
//        // Bổ sung id vào URL khi thực hiện PUT
//        String url = apiUrl + "/changePassword/" + changePassword.getId();
//
//        // Tạo một HttpEntity với thông tin Casher cập nhật để gửi yêu cầu PUT
//        HttpEntity<Casher> request = new HttpEntity<>(casher, headers);
//
//        try {
//            restTemplate.exchange(url, HttpMethod.PUT, request, Casher.class);
//            return "redirect:/admin/casher";
//
//        } catch (RestClientException e) {
//            return "/admin/casher/changepassword/" + changePassword.getId();
//        }
//    }

    @RequestMapping(value = "/admin/casher/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable("id") Integer id, HttpServletRequest request, HttpSession session) {

        Casher casher = casherService.getCasherById(id);

        Admin currentAdmin = authService.isAuthenticatedAdmin(request);
        Doctor currentDoctor = authService.isAuthenticatedDoctor(request);
        Patient currentPatient = authService.isAuthenticatedPatient(request);
        Casher currentCasher = authService.isAuthenticatedCasher(request);

        if (currentPatient != null && currentPatient.getRole().equals("PATIENT")) {
            return "redirect:/forbien";
        } else if (currentAdmin != null && currentAdmin.getRole().equals("ADMIN")) {
            if (casher.getTaophieukhamCollection().isEmpty()) {
                restTemplate.delete(apiUrl + "/" + id);
                // Thực hiện thêm xử lý sau khi xóa Casher thành công (nếu cần)

                // Chuyển hướng về trang danh sách Casher
                return "redirect:/admin/casher";
            } else {
                session.setAttribute("error", "This Casher cannot be deleted because this Casher has created a medical examination form.");
                return "redirect:/admin/casher";
            }
        } else if (currentDoctor != null && currentDoctor.getRole().equals("DOCTOR")) {
            if (casher.getTaophieukhamCollection().isEmpty()) {
                restTemplate.delete(apiUrl + "/" + id);
                // Thực hiện thêm xử lý sau khi xóa Casher thành công (nếu cần)

                // Chuyển hướng về trang danh sách Casher
                return "redirect:/admin/casher";
            } else {
                session.setAttribute("error", "This Casher cannot be deleted because this Casher has created a medical examination form.");
                return "redirect:/admin/casher";
            }
        } else if (currentCasher != null && currentCasher.getRole().equals("CASHER")) {
            if (casher.getTaophieukhamCollection().isEmpty()) {
                restTemplate.delete(apiUrl + "/" + id);
                // Thực hiện thêm xử lý sau khi xóa Casher thành công (nếu cần)

                // Chuyển hướng về trang danh sách Casher
                return "redirect:/admin/casher";
            } else {
                session.setAttribute("error", "This Casher cannot be deleted because this Casher has created a medical examination form.");
                return "redirect:/admin/casher";
            }
        } else {
            return "redirect:/login";
        }
    }

}
