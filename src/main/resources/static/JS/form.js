function findChecked() {

    const checkedRadios = [];
    const questionIds = [];
    const answerInput = document.querySelector('#allAnswers');
    const questionIdInput = document.querySelector('#questionIds');
    const radios = document.querySelectorAll('input[type=radio]');
    radios.forEach(radio =>{
    if(radio.checked){
        checkedRadios.push(radio.value);
        questionIds.push(radio.dataset.questionId);

        }

    });
    answerInput.value = checkedRadios;
    questionIdInput.value = questionIds;
//    return checkedRadios;
    console.log(checkedRadios);
    console.log(questionIds);
}

//let selectedAnswers = [];
const submitButton = document.querySelector('#button');
const answerInput = document.querySelector('#allAnswers')
    submitButton.addEventListener('click', (event) => {
//        selectedAnswers = findChecked();
        findChecked();
//        answerInput.value = selectedAnswers;


});



//function getRadioVal(form, name) {
//    var val;
//    // get list of radio buttons with specified name
//    var radios = form.elements[name];
//
//    // loop through list of radio buttons
//    for (var i=0, len=radios.length; i<len; i++) {
//        if ( radios[i].checked ) { // radio checked?
//            val = radios[i].value; // if so, hold its value in val
//            break; // and break out of for loop
//        }
//    }
//    return val; // return value of checked radio or undefined if none checked
//}



