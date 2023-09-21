/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package group2.client.controller;

import group2.client.entities.Admin;
import group2.client.entities.Casher;
import group2.client.entities.Doctor;
import group2.client.entities.Patient;
import group2.client.entities.Taophieukham;
import group2.client.entities.TypeDoctor;
import group2.client.service.AuthService;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Ann
 */
@Controller
@RequestMapping("/admin/phieukham")
public class TaophieukhamController {

    private String apiUrl = "http://localhost:8888/api/taophieukham/";
    private String apiUrlCasher = "http://localhost:8888/api/casher/";
    private String apiUrlTypeDoctor = "http://localhost:8888/api/typedoctor/";
    private String apiUrlPatient = "http://localhost:8888/api/patient/";
    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AuthService authService;

    @RequestMapping("")
    public String page(Model model, HttpServletRequest request) {

        Admin currentAdmin = authService.isAuthenticatedAdmin(request);
        Doctor currentDoctor = authService.isAuthenticatedDoctor(request);
        Patient currentPatient = authService.isAuthenticatedPatient(request);
        Casher currentCasher = authService.isAuthenticatedCasher(request);

        if (currentPatient != null && currentPatient.getRole().equals("PATIENT")) {
            return "redirect:/forbien";
        } else if (currentAdmin != null && currentAdmin.getRole().equals("ADMIN")) {
            ResponseEntity<List<Taophieukham>> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Taophieukham>>() {
            });

            // Kiểm tra mã trạng thái của phản hồi
            if (response.getStatusCode().is2xxSuccessful()) {
                List<Taophieukham> listTaophieukham = response.getBody();

                // Xử lý dữ liệu theo nhu cầu của bạn
                model.addAttribute("listTaophieukham", listTaophieukham);
                model.addAttribute("currentAdmin", currentAdmin);
            }
            return "admin/phieukham/index";
        } else if (currentDoctor != null && currentDoctor.getRole().equals("DOCTOR")) {
            ResponseEntity<List<Taophieukham>> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Taophieukham>>() {
            });

            // Kiểm tra mã trạng thái của phản hồi
            if (response.getStatusCode().is2xxSuccessful()) {
                List<Taophieukham> listTaophieukham = response.getBody();

                // Xử lý dữ liệu theo nhu cầu của bạn
                model.addAttribute("listTaophieukham", listTaophieukham);
                model.addAttribute("currentDoctor", currentDoctor);
            }
            return "admin/phieukham/index";
        } else if (currentCasher != null && currentCasher.getRole().equals("CASHER")) {
            ResponseEntity<List<Taophieukham>> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Taophieukham>>() {
            });

            // Kiểm tra mã trạng thái của phản hồi
            if (response.getStatusCode().is2xxSuccessful()) {
                List<Taophieukham> listTaophieukham = response.getBody();

                // Xử lý dữ liệu theo nhu cầu của bạn
                model.addAttribute("listTaophieukham", listTaophieukham);
                model.addAttribute("currentCasher", currentCasher);
            }
            return "admin/phieukham/index";
        } else {
            return "redirect:/login";
        }

    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model, HttpServletRequest request) {

        Admin currentAdmin = authService.isAuthenticatedAdmin(request);
        Doctor currentDoctor = authService.isAuthenticatedDoctor(request);
        Patient currentPatient = authService.isAuthenticatedPatient(request);
        Casher currentCasher = authService.isAuthenticatedCasher(request);

        if (currentPatient != null && currentPatient.getRole().equals("PATIENT")) {
            return "redirect:/forbien";
        } else if (currentAdmin != null && currentAdmin.getRole().equals("ADMIN")) {
            ResponseEntity<List<Casher>> casherResponse = restTemplate.exchange(apiUrlCasher, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Casher>>() {
            });
            ResponseEntity<List<TypeDoctor>> TDResponse = restTemplate.exchange(apiUrlTypeDoctor, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<TypeDoctor>>() {
            });
            ResponseEntity<List<Patient>> patientResponse = restTemplate.exchange(apiUrlPatient, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Patient>>() {
            });

            if (casherResponse.getStatusCode().is2xxSuccessful() && TDResponse.getStatusCode().is2xxSuccessful() && patientResponse.getStatusCode().is2xxSuccessful()) {
                List<Casher> listCasher = casherResponse.getBody();
                List<TypeDoctor> listTypeDoctor = TDResponse.getBody();
                List<Patient> listPatient = patientResponse.getBody();
                model.addAttribute("listTypeDoctor", listTypeDoctor);
                model.addAttribute("listCasher", listCasher);
                model.addAttribute("listPatient", listPatient);
                model.addAttribute("currentAdmin", currentAdmin);
            }

            model.addAttribute("taophieukham", new Taophieukham());
            return "admin/phieukham/create";
        } else if (currentDoctor != null && currentDoctor.getRole().equals("DOCTOR")) {
            ResponseEntity<List<Casher>> casherResponse = restTemplate.exchange(apiUrlCasher, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Casher>>() {
            });
            ResponseEntity<List<TypeDoctor>> TDResponse = restTemplate.exchange(apiUrlTypeDoctor, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<TypeDoctor>>() {
            });
            ResponseEntity<List<Patient>> patientResponse = restTemplate.exchange(apiUrlPatient, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Patient>>() {
            });

            if (casherResponse.getStatusCode().is2xxSuccessful() && TDResponse.getStatusCode().is2xxSuccessful() && patientResponse.getStatusCode().is2xxSuccessful()) {
                List<Casher> listCasher = casherResponse.getBody();
                List<TypeDoctor> listTypeDoctor = TDResponse.getBody();
                List<Patient> listPatient = patientResponse.getBody();
                model.addAttribute("listTypeDoctor", listTypeDoctor);
                model.addAttribute("listCasher", listCasher);
                model.addAttribute("listPatient", listPatient);
                model.addAttribute("currentDoctor", currentDoctor);
            }

            model.addAttribute("taophieukham", new Taophieukham());
            return "admin/phieukham/create";
        } else if (currentCasher != null && currentCasher.getRole().equals("CASHER")) {
            ResponseEntity<List<Casher>> casherResponse = restTemplate.exchange(apiUrlCasher, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Casher>>() {
            });
            ResponseEntity<List<TypeDoctor>> TDResponse = restTemplate.exchange(apiUrlTypeDoctor, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<TypeDoctor>>() {
            });
            ResponseEntity<List<Patient>> patientResponse = restTemplate.exchange(apiUrlPatient, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Patient>>() {
            });

            if (casherResponse.getStatusCode().is2xxSuccessful() && TDResponse.getStatusCode().is2xxSuccessful() && patientResponse.getStatusCode().is2xxSuccessful()) {
                List<Casher> listCasher = casherResponse.getBody();
                List<TypeDoctor> listTypeDoctor = TDResponse.getBody();
                List<Patient> listPatient = patientResponse.getBody();
                model.addAttribute("listTypeDoctor", listTypeDoctor);
                model.addAttribute("listCasher", listCasher);
                model.addAttribute("listPatient", listPatient);
                model.addAttribute("currentCasher", currentCasher);
            }

            model.addAttribute("taophieukham", new Taophieukham());
            return "admin/phieukham/create";
        } else {
            return "redirect:/login";
        }

    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(Model model, @Valid @ModelAttribute Taophieukham taophieukham, BindingResult bindingResult, @RequestParam("casherID") String casherID, @RequestParam("typeDoctorID") String typeDoctorID, @RequestParam("patientID") String patientID) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Lấy danh sách phiếu khám hiện có từ API server
        ResponseEntity<List<Taophieukham>> responseList = restTemplate.exchange(apiUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Taophieukham>>() {
        });
        List<Taophieukham> existingTaophieukhams = responseList.getBody();

        // Tìm số thứ tự lớn nhất trong danh sách phiếu khám
        int maxSoThuTu = existingTaophieukhams.stream().mapToInt(Taophieukham::getSothutu).max().orElse(0);

        // Tăng số thứ tự lên một để tạo phiếu khám mới
        taophieukham.setSothutu(maxSoThuTu + 1);

        Casher newCasher = new Casher();
        TypeDoctor newTD = new TypeDoctor();

        if (casherID == "") {
            newCasher.setId(null);
        } else {
            newCasher.setId(Integer.parseInt(casherID));
            taophieukham.setCasherId(newCasher);
        }

        if (typeDoctorID == "") {
            newTD.setId(null);
        } else {
            newTD.setId(Integer.parseInt(typeDoctorID));
            taophieukham.setTypeDoctorId(newTD);
        }

        if (patientID == "") {
            taophieukham.setPatientId(null);
        } else {
            taophieukham.setPatientId(Integer.parseInt(patientID));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("taophieukham", taophieukham);
            return "/admin/phieukham/create";
        }

        // Tạo một HttpEntity với thông tin Casher để gửi yêu cầu POST
        HttpEntity<Taophieukham> request = new HttpEntity<>(taophieukham, headers);

        ResponseEntity<Taophieukham> response = restTemplate.exchange(apiUrl + "create", HttpMethod.POST, request, Taophieukham.class);

        // Kiểm tra mã trạng thái của phản hồi
        if (response.getStatusCode().is2xxSuccessful()) {
            // Chuyển hướng về trang danh sách Taophieukham
            return "redirect:/admin/phieukham";
        } else {
            // Xử lý lỗi nếu cần thiết
            model.addAttribute("taophieukham", taophieukham);
            return "/admin/phieukham/create";
        }
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable("id") int id, HttpServletRequest request) {

        Admin currentAdmin = authService.isAuthenticatedAdmin(request);
        Doctor currentDoctor = authService.isAuthenticatedDoctor(request);
        Patient currentPatient = authService.isAuthenticatedPatient(request);
        Casher currentCasher = authService.isAuthenticatedCasher(request);

        if (currentPatient != null && currentPatient.getRole().equals("PATIENT")) {
            return "redirect:/forbien";
        } else if (currentAdmin != null && currentAdmin.getRole().equals("ADMIN")) {
            Taophieukham taophieukham = restTemplate.getForObject(apiUrl + "/" + id, Taophieukham.class);

            ResponseEntity<List<Casher>> casherResponse = restTemplate.exchange(apiUrlCasher, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Casher>>() {
            });
            ResponseEntity<List<TypeDoctor>> TDResponse = restTemplate.exchange(apiUrlTypeDoctor, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<TypeDoctor>>() {
            });

            if (casherResponse.getStatusCode().is2xxSuccessful() && TDResponse.getStatusCode().is2xxSuccessful()) {
                List<Casher> listCasher = casherResponse.getBody();
                List<TypeDoctor> listTypeDoctor = TDResponse.getBody();
                model.addAttribute("listTypeDoctor", listTypeDoctor);
                model.addAttribute("listCasher", listCasher);
                model.addAttribute("taophieukham", taophieukham);
                model.addAttribute("currentAdmin", currentAdmin);
                return "/admin/phieukham/edit";
            } else {
                return "redirect:/admin/phieukham";
            }
        } else if (currentDoctor != null && currentDoctor.getRole().equals("DOCTOR")) {
            Taophieukham taophieukham = restTemplate.getForObject(apiUrl + "/" + id, Taophieukham.class);

            ResponseEntity<List<Casher>> casherResponse = restTemplate.exchange(apiUrlCasher, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Casher>>() {
            });
            ResponseEntity<List<TypeDoctor>> TDResponse = restTemplate.exchange(apiUrlTypeDoctor, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<TypeDoctor>>() {
            });

            if (casherResponse.getStatusCode().is2xxSuccessful() && TDResponse.getStatusCode().is2xxSuccessful()) {
                List<Casher> listCasher = casherResponse.getBody();
                List<TypeDoctor> listTypeDoctor = TDResponse.getBody();
                model.addAttribute("listTypeDoctor", listTypeDoctor);
                model.addAttribute("listCasher", listCasher);
                model.addAttribute("taophieukham", taophieukham);
                model.addAttribute("currentDoctor", currentDoctor);
                return "/admin/phieukham/edit";
            } else {
                return "redirect:/admin/phieukham";
            }
        } else if (currentCasher != null && currentCasher.getRole().equals("CASHER")) {
            Taophieukham taophieukham = restTemplate.getForObject(apiUrl + "/" + id, Taophieukham.class);

            ResponseEntity<List<Casher>> casherResponse = restTemplate.exchange(apiUrlCasher, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Casher>>() {
            });
            ResponseEntity<List<TypeDoctor>> TDResponse = restTemplate.exchange(apiUrlTypeDoctor, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<TypeDoctor>>() {
            });

            if (casherResponse.getStatusCode().is2xxSuccessful() && TDResponse.getStatusCode().is2xxSuccessful()) {
                List<Casher> listCasher = casherResponse.getBody();
                List<TypeDoctor> listTypeDoctor = TDResponse.getBody();
                model.addAttribute("listTypeDoctor", listTypeDoctor);
                model.addAttribute("listCasher", listCasher);
                model.addAttribute("taophieukham", taophieukham);
                model.addAttribute("currentCasher", currentCasher);
                return "/admin/phieukham/edit";
            } else {
                return "redirect:/admin/phieukham";
            }
        } else {
            return "redirect:/login";
        }

    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String update(Model model, @Valid @ModelAttribute Taophieukham updatedTaophieukham, BindingResult bindingResult, @RequestParam String typeDoctorID) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Lấy phiếu khám hiện có từ API server
        Taophieukham existingTaophieukham = restTemplate.getForObject(apiUrl + "/" + updatedTaophieukham.getId(), Taophieukham.class);

        ResponseEntity<List<TypeDoctor>> TDResponse = restTemplate.exchange(apiUrlTypeDoctor, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<TypeDoctor>>() {
        });

        TypeDoctor newTD = new TypeDoctor();

        if (typeDoctorID == "") {
            newTD.setId(null);
        } else {
            newTD.setId(Integer.parseInt(typeDoctorID));
            updatedTaophieukham.setTypeDoctorId(newTD);
        }

        if (bindingResult.hasErrors()) {
            List<TypeDoctor> listTypeDoctor = TDResponse.getBody();
            model.addAttribute("listTypeDoctor", listTypeDoctor);
            model.addAttribute("taophieukham", updatedTaophieukham);
            return "/admin/phieukham/edit";
        }

        // Bổ sung id vào URL khi thực hiện PUT
        String url = apiUrl + "edit/" + updatedTaophieukham.getId(); // Đảm bảo URL đúng

        // Tạo một HttpEntity với thông tin phiếu khám cập nhật để gửi yêu cầu PUT
        HttpEntity<Taophieukham> request = new HttpEntity<>(updatedTaophieukham, headers);

        // Thực hiện PUT request để cập nhật phiếu khám
        restTemplate.exchange(url, HttpMethod.PUT, request, Taophieukham.class);
        // Sau khi cập nhật thành công, chuyển hướng về trang danh sách phiếu khám
        return "redirect:/admin/phieukham";

    }

    @RequestMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id") Integer id, HttpServletRequest request) {

        Admin currentAdmin = authService.isAuthenticatedAdmin(request);
        Doctor currentDoctor = authService.isAuthenticatedDoctor(request);
        Patient currentPatient = authService.isAuthenticatedPatient(request);
        Casher currentCasher = authService.isAuthenticatedCasher(request);

        if (currentPatient != null && currentPatient.getRole().equals("PATIENT")) {
            return "redirect:/forbien";
        } else if (currentAdmin != null && currentAdmin.getRole().equals("ADMIN")) {
            restTemplate.delete(apiUrl + "delete/" + id);
            // Thực hiện thêm xử lý sau khi xóa Taophieukham thành công (nếu cần)

            // Chuyển hướng về trang danh sách Taophieukham
            return "redirect:/admin/phieukham";
        } else if (currentDoctor != null && currentDoctor.getRole().equals("DOCTOR")) {
            restTemplate.delete(apiUrl + "delete/" + id);
            // Thực hiện thêm xử lý sau khi xóa Taophieukham thành công (nếu cần)

            // Chuyển hướng về trang danh sách Taophieukham
            return "redirect:/admin/phieukham";
        } else if (currentCasher != null && currentCasher.getRole().equals("CASHER")) {
            restTemplate.delete(apiUrl + "delete/" + id);
            // Thực hiện thêm xử lý sau khi xóa Taophieukham thành công (nếu cần)

            // Chuyển hướng về trang danh sách Taophieukham
            return "redirect:/admin/phieukham";
        } else {
            return "redirect:/login";
        }

    }
}
