package com.govnet.govnet.controller;

import com.govnet.govnet.dto.DepartmentDTO;
import com.govnet.govnet.dto.DepartmentResponseDto;
import com.govnet.govnet.dto.DropDownDTO;
import com.govnet.govnet.entity.Department;
import com.govnet.govnet.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentSer;

    @PostMapping("/add")
    public String addDepartment(@RequestBody DepartmentDTO department1) {
        departmentSer.addDepartment(department1);
        return "Department added successfully";
    }

    @GetMapping("/getById/{id}")
    public DepartmentDTO getSingleDepartment(@PathVariable Long id) {
        return departmentSer.getSingleDepartment(id);
    }

    @GetMapping("/all")
    public Page<DepartmentResponseDto> getAllDepartments(Pageable pageable) {

        // Page<SendDoc> page = sendDocumentRepo.findAll(spec, pageable);
        // List<SendDocResponseDTO> sendDocResponseDTOS = mapEntityToDTO(page.getContent());
        // // Pageable pageable2 = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize(), pageable.getSort());
        // return new PageImpl<>(sendDocResponseDTOS, pageable, page.getTotalElements());
        // @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size
        if(pageable.getPageSize()!=0){

        }
        return departmentSer.getMultipleDepartments(pageable);
        // Long totalRecords = departmentSer.totalRecordsInDepartment();
        // return new APIResponse<>(multipleDepartments.getSize(),totalRecords,multipleDepartments);
    }

    @GetMapping("/allForChart")
    public List<DepartmentResponseDto> getAllDepartments() {
        return departmentSer.getDepartmentForChart();
    }

    @GetMapping("/AllDropDownDepartment")
    public List<DropDownDTO> AllDepartmentsForDropDown() {
        return departmentSer.getAllDepartment();
    }

    @GetMapping("/getDepartmentsByParentDepartment/{id}")
    public List<Department> getDepartmentsByParent(@PathVariable Long id){
        return  departmentSer.getDepartmentByParentId(id);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<String> updateDepartment(@PathVariable Long id,@RequestBody DepartmentDTO updateDepartment){
        departmentSer.updateDepartment(id,updateDepartment);
        return ResponseEntity.ok("Department updated successfully");
    }

    @GetMapping("/firstChild/{parentDepartmentId}")
    public ResponseEntity<List<DepartmentResponseDto>> getFirstChild(@PathVariable Long parentDepartmentId) {
        List<DepartmentResponseDto> firstChildDepartment = departmentSer.findFirstChildDepartment(parentDepartmentId);
        return ResponseEntity.ok(firstChildDepartment);
    }
    @GetMapping("/siblings")
    public ResponseEntity<List<DepartmentResponseDto>> findSiblings(){
        List<DepartmentResponseDto> mySiblings = departmentSer.findMySiblings();
        return ResponseEntity.ok(mySiblings);
    }
    @GetMapping("/search")
    public ResponseEntity<List<DepartmentResponseDto>> searchDepartment(@RequestParam String searchItem){
        List<DepartmentResponseDto> departmentResponseDtos = departmentSer.searchDepartments(searchItem);
        return ResponseEntity.ok(departmentResponseDtos);
    }


}
