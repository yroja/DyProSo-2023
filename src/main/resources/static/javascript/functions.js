//navbar dropdown
$(function(){
    $('.ui.dropdown').dropdown({transition: 'drop', on: 'hover' });
});

$(function(){
   $('.ui.calendar').calendar();
});

$(function(){
    $('.ui.checkbox').checkbox();
});

//sidebar functions
$(function(){
    $('#about').click(function(){
        $('#submenu0').transition('slide down');
        if($('#submenu1').is(":visible")){
            $('#submenu1').transition('slide down');
        }
        if($('#submenu2').is(":visible")){
                    $('#submenu2').transition('slide down');
                }
      });
});
$(function(){
    $('#programme').click(function(){
        $('#submenu1').transition('slide down');
        if($('#submenu0').is(":visible")){
                    $('#submenu0').transition('slide down');
                }
        if($('#submenu2').is(":visible")){
                            $('#submenu2').transition('slide down');
                        }
      });
});
$(function(){
    $('#participants').click(function(){
        $('#submenu2').transition('slide down');
        if($('#submenu0').is(":visible")){
                    $('#submenu0').transition('slide down');
                }
        if($('#submenu1').is(":visible")){
                            $('#submenu1').transition('slide down');
                        }
      });
});

$(function(){
    $('#sidebar_menu').click(function(){
            $('.ui.sidebar').sidebar('setting', 'transition', 'overlay').sidebar('attach events', '#fixed_menu1');
            $('#submenu0').hide();
            $('#submenu1').hide();
            $('#submenu2').hide();
        });
});



$(function(){
    $('.ui.radio.checkbox').checkbox();
});

let slideIndex = 1;
$(function(){

     showSlides(slideIndex);
     function showSlides(n) {
           let i;
           let slides = document.getElementsByClassName("mySlides");
           let dots = document.getElementsByClassName("dot");
           if (n > slides.length) {slideIndex = 1}
           if (n < 1) {slideIndex = slides.length}
           for (i = 0; i < slides.length; i++) {
             slides[i].style.display = "none";
           }
           for (i = 0; i < dots.length; i++) {
             dots[i].className = dots[i].className.replace(" active", "");
           }
           slides[slideIndex-1].style.display = "block";
           dots[slideIndex-1].className += " active";
         }
});

    function hide_form(click){
            var clicked = click.checked;
            if(clicked){
                document.getElementById('excursion_form').style.display = "none";
            }
        }

    function show_form(click){
                var clicked = click.checked;
                if(clicked){
                    document.getElementById('excursion_form').style.display = "block";
                }
            }

    function show_billing_address_form_in_EU(click){
                var clicked = click.checked;
                if(clicked){
                    document.getElementById('billing_address_form').style.display = "block";
                    document.getElementById('vat_number_form').style.display = "block";
                }
            }

    function show_billing_address_form_non_EU(click){
                var clicked = click.checked;
                if(clicked){
                    document.getElementById('billing_address_form').style.display = "block";
                    document.getElementById('vat_number_form').style.display = "none";
                }
            }


    // Next/previous controls
    function plusSlides(n) {
      showSlides(slideIndex += n);
    }

    // Thumbnail image controls
    function currentSlide(n) {
      showSlides(slideIndex = n);
    }

    function showSlides(n) {
      let i;
      let slides = document.getElementsByClassName("mySlides");
      let dots = document.getElementsByClassName("dot");
      if (n > slides.length) {slideIndex = 1}
      if (n < 1) {slideIndex = slides.length}
      for (i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";
      }
      for (i = 0; i < dots.length; i++) {
        dots[i].className = dots[i].className.replace(" active", "");
      }
      slides[slideIndex-1].style.display = "block";
      dots[slideIndex-1].className += " active";
    }

/*
$(window).scroll(function() {
        if ($(this).scrollTop() < 260 ) {
            $('#fixed_menu1').addClass("sticky");
        }
        $('#fixed_menu1').removeClass("fixed");
        $('#fixed_menu1').addClass("sticky");
});*/

