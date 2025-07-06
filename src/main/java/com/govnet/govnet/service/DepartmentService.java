package com.govnet.govnet.service;

import com.govnet.govnet.dto.DepartmentDTO;
import com.govnet.govnet.dto.DepartmentResponseDto;
import com.govnet.govnet.dto.DepwithUserDto;
import com.govnet.govnet.dto.DropDownDTO;
import com.govnet.govnet.entity.Department;
import com.govnet.govnet.repo.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepo;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private CurrentUserInfoService currentUserInfoService;

    public void addDepartment(DepartmentDTO departmentdto) {
        Department department1 = new Department();
        department1.setDepName(departmentdto.getDepName());
        department1.setDescription(departmentdto.getDescription());
        Long parentDepartmentId = departmentdto.getParentDepartmentId();
        if (parentDepartmentId != null)
            department1.setParentDepartmentId(getDepartment(parentDepartmentId));
        Department save;
        save = departmentRepo.save(department1);
        List<Department> departmentList=new ArrayList<>();
        departmentList.add(save);
        Map<String,Object> details=new HashMap<>();
        details.put("new added department Name ",save.getDepName());
//      ActivityLog activityLog=new ActivityLog();
//      String content = activityLog.MapToString(details);
//      activityLogService.logsActivity("Department",department1.getDepId(),"Created",departmentList,content);

    }

    public DepartmentDTO getSingleDepartment(Long id) {
        return departmentRepo.findById(id)
                .map(department -> {
                    DepartmentDTO map = mapper.map(department, DepartmentDTO.class);
                    return map;
                })
                .orElse(new DepartmentDTO()); // Provide a default departmentDTO when the department is not found
    }

    public Page<DepartmentResponseDto> getMultipleDepartments(org.springframework.data.domain.Pageable pageable) {

        Page<Department> departmentsList = departmentRepo.findAll(pageable);

        // if pageNumber and pageSize is not null then paginated data will return
        // if (pageNumber != null && pageSize != null) {
        // pageNumber=pageNumber-1;
        // if (pageNumber <0){
        // // Return a message when pageNumber is zero or less than zero
        // String errorMessage = "Invalid pageNumber. Please provide a pageNumber
        // greater than zero.";
        // throw new IllegalArgumentException(errorMessage);
        // }

        // Page<Department> departments =
        // departmentRepo.findAll(PageRequest.of(pageNumber, pageSize,
        // Sort.by(Direction.ASC, "depId")));
        // departmentsList = departments.getContent();
        // }
        // else {
        // departmentsList = departmentRepo.findAll();
        // }
        List<DepartmentResponseDto> collect = departmentsList.stream().map(department -> {
            DepartmentResponseDto map = mapper.map(department, DepartmentResponseDto.class);
            if (department.getParentDepartmentId() != null) {
                DepwithUserDto parent = new DepwithUserDto(department.getParentDepartmentId().getDepId(),
                        department.getParentDepartmentId().getDepName());
                map.setParent(parent);
            }
            return map;
        })
                .collect(Collectors.toList());

        return new PageImpl<>(collect, pageable, departmentsList.getTotalElements());
    }

    public Department getDepartment(Long id) {
        Department byId = departmentRepo.findById(id).orElseThrow();
        return byId;
    }

    public List<Department> getDepartmentByParentId(Long id) {
        Optional<Department> byId = departmentRepo.findById(id);
        if (byId.isPresent())
            return departmentRepo.findByparentDepartmentId(byId.get());
        else {
            return Collections.emptyList();
        }
    }

    public void updateDepartment(Long id, DepartmentDTO updatedDepartment) {
        Department department = departmentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("not found department with id " + id));
        Department previousDepartment = new Department();
        Map<String,Object> updatedData = new HashMap<>();
        if (department != null) {
            if (updatedDepartment.getDepName() != null) {
                department.setDepName(updatedDepartment.getDepName());
                if (!Objects.equals(previousDepartment.getDepName(),department.getDepName())){
                    updatedData.put("previous department Name ",previousDepartment.getDepName());
                    updatedData.put("updated department Name ",department.getDepName());
                }

            }
            if (updatedDepartment.getDescription() != null) {
                department.setDescription(updatedDepartment.getDescription());
                if (!Objects.equals(previousDepartment.getDescription(),department.getDescription())){
                    updatedData.put("previous department Description ",previousDepartment.getDescription());
                    updatedData.put("updated department Description ",department.getDescription());
                }

            }
            if (updatedDepartment.getParentDepartmentId() != null) {
                department.setParentDepartmentId(this.getDepartment(updatedDepartment.getParentDepartmentId()));
                if (previousDepartment.getParentDepartmentId() != department.getParentDepartmentId()){
                    updatedData.put("previous department parent Department Name ",previousDepartment.getParentDepartmentId().getDepName());
                    updatedData.put("updated department parent Department Name ",department.getDepName());
                }
            }
        }
        Department update = departmentRepo.save(department);
        List<Department> departmentList=new ArrayList<>();
        departmentList.add(update);
//      ActivityLog activityLog=new ActivityLog();
//      String content = activityLog.MapToString(updatedData);
//      activityLogService.logsActivity("Department",updatedDepartment.getDepId(),"Updated",departmentList,content);
    }

    public Long totalRecordsInDepartment() {
        return departmentRepo.count();
    }

    public List<DepartmentResponseDto> findFirstChildDepartment(Long parentDepartmentId) {
        List<Department> firstChildDepartment = departmentRepo.findFirstChildDepartment(parentDepartmentId);
        List<DepartmentResponseDto> collect = firstChildDepartment.stream().map(department -> {
            DepartmentResponseDto departmentResponseDto = mapEntityToDTO(department);
            return departmentResponseDto;
        }).collect(Collectors.toList());
        // DepartmentResponseDto departmentResponseDto =
        // mapEntityToDTO(firstChildDepartment);
        return collect;
    }
    public List<DepartmentResponseDto> findMySiblings() {
        List<Department> currentUserDepartments = currentUserInfoService.getCurrentUser().getDepartment();

        List<Long> currentUserParentDepIds = currentUserDepartments.stream().map(department -> {
            Long currentUserParentDepId = department.getParentDepartmentId().getDepId();
            return currentUserParentDepId;
        }).collect(Collectors.toList());

        List<Department> allSiblings = departmentRepo.findAllSiblings(currentUserParentDepIds);
        List<DepartmentResponseDto> siblingDepartmentList = allSiblings.stream().map(department1 -> {
            DepartmentResponseDto departmentResponseDto = mapEntityToDTO(department1);
            return departmentResponseDto;
        }).collect(Collectors.toList());
        return siblingDepartmentList;
    }

    private DepartmentResponseDto mapEntityToDTO(Department department) {
        DepartmentResponseDto map = mapper.map(department, DepartmentResponseDto.class);
        if (department.getParentDepartmentId() !=null) {
            map.setParent(new DepwithUserDto(department.getParentDepartmentId().getDepId(),
                    department.getParentDepartmentId().getDepName()));
        }
        else {
            map.setParent(null);
        }
        return map;
    }
    // public List<department> byUserId(Long id){
    // return departmentRepo.findByUsersId(id);
    // }

    public List<DropDownDTO> getAllDepartment() {
        List<Department> firstChildDepartment = departmentRepo.findAll();
        List<DropDownDTO> collect = firstChildDepartment.stream().map(department -> {
            DropDownDTO dropDownDTO = mapper.map(department, DropDownDTO.class);
            return dropDownDTO;
        }).collect(Collectors.toList());
        return collect;

    }

    public List<DepartmentResponseDto> getDepartmentForChart() {
        List<Department> departmentsList = departmentRepo.findAll();
        List<DepartmentResponseDto> collect = departmentsList.stream().map(department -> {
                DepartmentResponseDto map = mapper.map(department, DepartmentResponseDto.class);
                if (department.getParentDepartmentId()!=null) {
                DepwithUserDto parent = new DepwithUserDto(department.getParentDepartmentId().getDepId(), department.getParentDepartmentId().getDepName());
                map.setParent(parent);
                }
                return map;
                })
                .collect(Collectors.toList());
        return collect;
    }

    public List<DepartmentResponseDto> searchDepartments(String searchItem) {
        List<Department> departmentList = departmentRepo.searchDepartments(searchItem);
        List<DepartmentResponseDto> departmentListResponseDTO = departmentList.stream().map(department -> {
            DepartmentResponseDto departmentResponseDto = mapEntityToDTO(department);
            return departmentResponseDto;
        }).collect(Collectors.toList());
        return departmentListResponseDTO;
    }
}
