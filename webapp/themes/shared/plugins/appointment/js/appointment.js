document.addEventListener('DOMContentLoaded', function () {
	const form = document.getElementById('form-validate');
	const submitButton = document.getElementById('save');

	if (!form || !submitButton) return;

	submitButton.addEventListener('click', function () {

		const requiredRadios = form.querySelectorAll('input[type="radio"][required]');
		let count = 0;

		requiredRadios.forEach(input => {
			const style = window.getComputedStyle(input);
			const isHidden = style.display === 'none' || style.visibility === 'hidden';
			const isDisabled = input.closest('fieldset:disabled') !== null;

			if (isHidden || isDisabled) {
				input.removeAttribute('required');
				count++;
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
