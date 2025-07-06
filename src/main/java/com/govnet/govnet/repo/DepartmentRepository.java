package com.govnet.govnet.repo;

import com.govnet.govnet.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department,Long>{
    List<Department> findByparentDepartmentId(Department parentDepartmentId);
    Page<Department> findAll(Pageable pageable);
    List<Department> findAll();
//    @Query("SELECT d FROM Department d WHERE d.parentDepartmentId.depId = :parentDepartmentId")
//    Department findFirstChildDepartment(@Param("parentDepartmentId") Long parentDepartmentId);

    @Query("SELECT d FROM Department d WHERE d.parentDepartmentId.depId = :parentDepartmentId")
    List<Department> findFirstChildDepartment(@Param("parentDepartmentId") Long parentDepartmentId);

    @Query("SELECT d FROM Department d WHERE d.parentDepartmentId.depId IN :currentUserDepartmentIds")
    List<Department> findAllSiblings(List<Long> currentUserDepartmentIds);
    @Query("SELECT d FROM Department d WHERE LOWER(d.depName) LIKE LOWER(CONCAT('%', :searchItem, '%'))")
    List<Department> searchDepartments(@Param("searchItem") String searchItem);
}
