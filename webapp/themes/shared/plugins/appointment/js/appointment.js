document.addEventListener('DOMContentLoaded', function () {
	const saveSelector = 'button[name="save"], input[type="submit"][name="save"], #save';

	document.querySelectorAll('form').forEach(form => {
		const saveBtn = form.querySelector(saveSelector);
		if (!saveBtn) return;

		saveBtn.addEventListener('click', function () {
			const requiredFields = form.querySelectorAll('input[required], select[required], textarea[required]');
			requiredFields.forEach(field => {
				const styles = window.getComputedStyle(field);
				const isHidden =
					field.offsetParent === null ||
					styles.display === 'none' ||
					styles.visibility === 'hidden';
				const isDisabled = field.disabled || field.closest('fieldset:disabled');

				if (isHidden || isDisabled) {
					field.removeAttribute('required');
					field.removeAttribute('aria-required');
					if (field.type === 'radio' && field.name) {
						form.querySelectorAll(`input[type="radio"][name="${CSS.escape(field.name)}"][required]`)
							.forEach(r => {
								r.removeAttribute('required');
								r.removeAttribute('aria-required');
							});
					}
				}
			});
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
