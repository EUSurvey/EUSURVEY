
	function confirmRestore(id, alias)
	{		
		$("#confirm-restore-dialog-target").attr("data-alias",alias);
		$("#confirm-restore-dialog-target").attr("href", contextpath + "/archive/restore/" + id);
		$("#confirm-restore-dialog").modal("show");
	}
	
	