function findChecked() {

    const checkedRadios = [];
    const questionIds = []; //arrays
    const answerInput = document.querySelector('#allAnswers'); //why is this declared twice again?
    const questionIdInput = document.querySelector('#questionIds');//sets variable for hidden input of questionIds
    const radios = document.querySelectorAll('input[type=radio]');// sets variable for all radio button values
    radios.forEach(radio =>{ //loops through radio values
    if(radio.checked){ // if the radio button is checked
        checkedRadios.push(radio.value); //push the value of radio button to checkedRadios array
        questionIds.push(radio.dataset.questionId);//push question.Id to questionIds array. Notice that dataset interprets...
                                                   //...question.id as questionId

        }

    });
    answerInput.value = checkedRadios; // pass array of checked radios to answerInput for the id of "allAnswers" hidden input
    questionIdInput.value = questionIds;// same as above but for id="questionIds" hidden input
    console.log(checkedRadios);//debug
    console.log(questionIds);
}






//Are these globals?
const submitButton = document.querySelector('#button'); //sets variable for button with ID of "buttton"
    submitButton.addEventListener('click', (event) => { //Adds the even for when button is clicked
        findChecked(); // calls function on click


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



