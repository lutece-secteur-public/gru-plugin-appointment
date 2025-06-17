document.addEventListener('DOMContentLoaded', function () {
	const form = document.getElementById('form-validate');
	const submitButton = document.getElementById('save');

	if (!form || !submitButton) return;

	submitButton.addEventListener('click', function () {

		const requiredFields = form.querySelectorAll('input[required], select[required], textarea[required]');

		requiredFields.forEach(field => {
			const isHidden = field.offsetParent === null ||
				window.getComputedStyle(field).display === 'none' ||
				window.getComputedStyle(field).visibility === 'hidden';

			const isDisabled = field.disabled || field.closest('fieldset:disabled');

			if (isHidden || isDisabled) {
				field.removeAttribute('required');
			}
		});

	});
});

function displayId(baliseId)
{
	if (document.getElementById && document.getElementById(baliseId) != null) {
		document.getElementById(baliseId).style.visibility='visible';
		document.getElementById(baliseId).style.display='block';
	}
}

function hideId(baliseId) {
	if (document.getElementById && document.getElementById(baliseId) != null) {
   		document.getElementById(baliseId).style.visibility='hidden';
    	document.getElementById(baliseId).style.display='none';
	}
}

function doDisplay(id)
{
	displayId("div"+id);
}

function hide(id)
{
     hideId("div"+id);
}

function doCheckboxEffect(isChecked,id) 
{
	if (isChecked) 
	{
		doDisplay(id);
	} 
	else 
	{
		hide(id);
	}
}

function changeDisabledStateOfRequiredFields(id, bDisabled)
{
	const divElement = document.getElementById("div"+id);
	
	if (divElement !== null)
	{
		divElement.querySelectorAll("[required]").forEach(element => {
			if (bDisabled)
				element.setAttribute('disabled', "");
			else
				element.removeAttribute("disabled");
    	});
	}
}
