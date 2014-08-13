@EnableEurekaClient
@EnableZuulProxy
@Controller
class Application extends WebMvcConfigurerAdapter {
  void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**").addResourceLocations('classpath:/ui', 'classpath:/src/main/resources/public/')
  }
  @RequestMapping("/")
  String home() { 
    return 'redirect:/index.html#/customers'
  }
}