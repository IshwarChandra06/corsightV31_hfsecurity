package com.eikona.mata.repository;
import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eikona.mata.entity.Area;
import com.eikona.mata.entity.Branch;

@Repository
public interface AreaRepository extends DataTablesRepository<Area, Long> {
	
	List<Area> findAllByIsDeletedFalse();
	
	Area findByIdAndIsDeletedFalse(long id);
	
	@Query("select b from com.eikona.mata.entity.Area as b where b.branch.name =:branch and isDeleted=false")
	List<Area> findByBranchAndIsDeletedFalseCustom(String branch);

	List<Area> findByBranchAndIsDeletedFalse(Branch branch);

	Area findByNameAndIsDeletedFalse(String area);

	Area findByNameAndBranchAndIsDeletedFalse(String area, Branch branch);

	Area findByWatchlistIdAndIsDeletedFalse(String string);

	List<Area> findAllByIsDeletedFalseAndIsSyncFalse();

	List<Area> findAllByIsDeletedTrueAndIsSyncTrue();

	List<Area> findAllByIsDeletedFalseAndIsSyncTrue();

	Area findByWatchlistAndIsDeletedFalse(String watchlist);

}
